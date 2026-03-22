import 'package:flutter/material.dart';
import 'package:smart_home/core/constants/app_theme.dart';

class AppBadge extends StatelessWidget {
  final String text;
  final String variant;
  final double fontSize;

  const AppBadge({
    Key? key,
    required this.text,
    this.variant = 'primary',
    this.fontSize = 12,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color backgroundColor;
    Color textColor;

    switch (variant) {
      case 'success':
        backgroundColor = const Color(0xFFECFDF5);
        textColor = AppColors.success;
        break;
      case 'warning':
        backgroundColor = const Color(0xFFFFFBEB);
        textColor = AppColors.warning;
        break;
      case 'error':
        backgroundColor = const Color(0xFFFEF2F2);
        textColor = AppColors.error;
        break;
      case 'neutral':
        backgroundColor = AppColors.gray200;
        textColor = AppColors.gray700;
        break;
      default:
        backgroundColor = AppColors.primaryLight;
        textColor = AppColors.primary;
    }

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.sm,
        vertical: AppSpacing.xs,
      ),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(AppRadius.tag),
      ),
      child: Text(
        text,
        style: TextStyle(
          fontSize: fontSize,
          fontWeight: FontWeight.w500,
          color: textColor,
        ),
      ),
    );
  }
}
