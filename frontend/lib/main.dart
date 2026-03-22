import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'core/services/app_state.dart';
import 'core/constants/app_theme.dart';
import 'modules/home/home_page.dart';

void main() {
  runApp(const SmartHomeApp());
}

class SmartHomeApp extends StatelessWidget {
  const SmartHomeApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => AppState()..init(),
      child: MaterialApp(
        title: '智能家居方案设计',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          primaryColor: AppColors.primary,
          scaffoldBackgroundColor: AppColors.gray100,
          colorScheme: ColorScheme.fromSeed(
            seedColor: AppColors.primary,
            primary: AppColors.primary,
            error: AppColors.error,
          ),
          appBarTheme: const AppBarTheme(
            backgroundColor: Colors.white,
            foregroundColor: AppColors.gray900,
            elevation: 0,
            centerTitle: true,
          ),
          fontFamily: 'PingFang SC',
        ),
        home: const HomePage(),
      ),
    );
  }
}
