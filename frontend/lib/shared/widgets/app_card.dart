import 'package:flutter/material.dart';
import 'package:smart_home/core/constants/app_theme.dart';

class AppCard extends StatelessWidget {
  final Widget child;
  final double padding;
  final bool clickable;
  final VoidCallback? onTap;
  final String? shadow;

  const AppCard({
    Key? key,
    required this.child,
    this.padding = AppSpacing.md,
    this.clickable = false,
    this.onTap,
    this.shadow = 'elevation-1',
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    BoxShadow? boxShadow;

    switch (shadow) {
      case 'elevation-2':
        boxShadow = const BoxShadow(
          color: Color(0x0F000000),
          blurRadius: 6,
          offset: Offset(0, 4),
        );
        break;
      case 'elevation-3':
        boxShadow = const BoxShadow(
          color: Color(0x1A000000),
          blurRadius: 15,
          offset: Offset(0, 10),
        );
        break;
      default:
        boxShadow = const BoxShadow(
          color: Color(0x0D000000),
          blurRadius: 2,
          offset: Offset(0, 1),
        );
    }

    return GestureDetector(
      onTap: clickable ? onTap : null,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        padding: EdgeInsets.all(padding),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(AppRadius.card),
          boxShadow: [boxShadow],
        ),
        child: child,
      ),
    );
  }
}
