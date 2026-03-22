import 'package:flutter/material.dart';
import 'package:smart_home/core/constants/app_theme.dart';

class AppButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final bool isLoading;
  final String variant;
  final String size;

  const AppButton({
    Key? key,
    required this.text,
    this.onPressed,
    this.isLoading = false,
    this.variant = 'primary',
    this.size = 'medium',
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final isDisabled = onPressed == null || isLoading;

    Color backgroundColor;
    Color textColor;

    switch (variant) {
      case 'primary':
        backgroundColor = isDisabled ? AppColors.gray300 : AppColors.primary;
        textColor = Colors.white;
        break;
      case 'secondary':
        backgroundColor = isDisabled ? AppColors.gray100 : Colors.white;
        textColor = isDisabled ? AppColors.gray500 : AppColors.primary;
        break;
      case 'text':
        backgroundColor = Colors.transparent;
        textColor = isDisabled ? AppColors.gray500 : AppColors.primary;
        break;
      case 'danger':
        backgroundColor = isDisabled ? AppColors.gray300 : AppColors.error;
        textColor = Colors.white;
        break;
      default:
        backgroundColor = AppColors.primary;
        textColor = Colors.white;
    }

    double height;
    double horizontalPadding;

    switch (size) {
      case 'small':
        height = 32;
        horizontalPadding = 12;
        break;
      case 'large':
        height = 48;
        horizontalPadding = 24;
        break;
      default:
        height = 40;
        horizontalPadding = 16;
    }

    return GestureDetector(
      onTap: isDisabled ? null : onPressed,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        height: height,
        padding: EdgeInsets.symmetric(horizontal: horizontalPadding),
        decoration: BoxDecoration(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(AppRadius.button),
          border: variant == 'secondary'
              ? Border.all(color: AppColors.primary, width: 1)
              : null,
        ),
        child: Center(
          child: isLoading
              ? SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(
                    strokeWidth: 2,
                    valueColor: AlwaysStoppedAnimation<Color>(textColor),
                  ),
                )
              : Text(
                  text,
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                    color: textColor,
                  ),
                ),
        ),
      ),
    );
  }
}
