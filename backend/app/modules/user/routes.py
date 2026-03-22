from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.core.security import verify_password, get_password_hash, create_access_token, create_refresh_token, decode_token
from app.core.config import settings
from app.modules.user.models import User
from app.modules.user.schemas import (
    UserCreate, UserResponse, LoginRequest, TokenResponse,
    RefreshTokenRequest, UserUpdate
)
from app.shared.schema import success_response, error_response

router = APIRouter(prefix="/api/v1/auth", tags=["认证"])
user_router = APIRouter(prefix="/api/v1/users", tags=["用户"])


@router.post("/register", response_model=dict)
def register(user_data: UserCreate, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter(User.phone == user_data.phone).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="该手机号已注册"
        )

    user = User(
        phone=user_data.phone,
        password_hash=get_password_hash(user_data.password),
        nickname=user_data.nickname
    )
    db.add(user)
    db.commit()
    db.refresh(user)

    return success_response(
        data={"user_id": user.id, "phone": user.phone},
        message="注册成功"
    )


@router.post("/login", response_model=dict)
def login(login_data: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.phone == login_data.phone).first()
    if not user or not verify_password(login_data.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="手机号或密码错误"
        )

    if user.status != 1:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="账户已被禁用"
        )

    access_token = create_access_token(user.id)
    refresh_token = create_refresh_token(user.id)

    return success_response(
        data={
            "access_token": access_token,
            "refresh_token": refresh_token,
            "expires_in": settings.ACCESS_TOKEN_EXPIRE_MINUTES * 60
        },
        message="登录成功"
    )


@router.post("/refresh", response_model=dict)
def refresh_token(token_data: RefreshTokenRequest, db: Session = Depends(get_db)):
    payload = decode_token(token_data.refresh_token)
    if payload is None or payload.get("type") != "refresh":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="无效的刷新令牌"
        )

    user_id = payload.get("sub")
    user = db.query(User).filter(User.id == int(user_id)).first()
    if not user or user.status != 1:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="用户不存在或已禁用"
        )

    new_access_token = create_access_token(user.id)
    new_refresh_token = create_refresh_token(user.id)

    return success_response(
        data={
            "access_token": new_access_token,
            "refresh_token": new_refresh_token,
            "expires_in": settings.ACCESS_TOKEN_EXPIRE_MINUTES * 60
        }
    )


@user_router.get("/profile", response_model=dict)
def get_profile(current_user: User = Depends(__import__("app.core.dependencies", fromlist=["get_current_user"]).get_current_user)):
    return success_response(
        data={
            "id": current_user.id,
            "phone": current_user.phone,
            "nickname": current_user.nickname,
            "avatar_url": current_user.avatar_url,
            "created_at": current_user.created_at.isoformat() if current_user.created_at else None
        }
    )


@user_router.put("/profile", response_model=dict)
def update_profile(
    user_data: UserUpdate,
    current_user: User = Depends(__import__("app.core.dependencies", fromlist=["get_current_user"]).get_current_user),
    db: Session = Depends(get_db)
):
    if user_data.nickname is not None:
        current_user.nickname = user_data.nickname
    if user_data.avatar_url is not None:
        current_user.avatar_url = user_data.avatar_url

    db.commit()
    db.refresh(current_user)

    return success_response(
        data={
            "id": current_user.id,
            "phone": current_user.phone,
            "nickname": current_user.nickname,
            "avatar_url": current_user.avatar_url
        },
        message="更新成功"
    )
