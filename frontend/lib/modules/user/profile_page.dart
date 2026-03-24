import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/services/app_state.dart';
import '../../core/services/logger_service.dart';
import '../../core/constants/app_theme.dart';
import '../../shared/models/questionnaire.dart';

class ProfilePage extends StatelessWidget {
  const ProfilePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('本地数据管理'),
      ),
      body: Consumer<AppState>(
        builder: (context, appState, child) {
          return ListView(
            padding: const EdgeInsets.all(AppSpacing.md),
            children: [
              _buildDataCard(
                context,
                icon: Icons.home_outlined,
                title: '户型数据',
                count: appState.houses.length,
                onClear: () => _showClearDialog(
                  context,
                  '户型数据',
                  appState.houses.length,
                  () async {
                    for (final house in appState.houses) {
                      await appState.deleteHouse(house.id);
                    }
                  },
                ),
              ),
              const SizedBox(height: AppSpacing.md),
              _buildDataCard(
                context,
                icon: Icons.lightbulb_outline,
                title: '智能方案',
                count: appState.schemes.length,
                onClear: () => _showClearDialog(
                  context,
                  '智能方案',
                  appState.schemes.length,
                  () async {
                    appState.schemes.clear();
                    await appState.clearAllData();
                  },
                ),
              ),
              const SizedBox(height: AppSpacing.md),
              _buildDataCard(
                context,
                icon: Icons.description_outlined,
                title: '问卷偏好',
                count: appState.questionnaire != null ? 1 : 0,
                onClear: () => _showClearDialog(
                  context,
                  '问卷偏好',
                  appState.questionnaire != null ? 1 : 0,
                  () async {
                    await appState.saveQuestionnaire(
                      Questionnaire(
                        livingStatus: 'own',
                        residentCount: 1,
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: AppSpacing.lg),
              _buildExportButton(context, appState),
              const SizedBox(height: AppSpacing.lg),
              _buildLogUploadCard(context),
              const SizedBox(height: AppSpacing.lg),
              _buildDangerZone(context, appState),
            ],
          );
        },
      ),
    );
  }

  Widget _buildDataCard(
    BuildContext context, {
    required IconData icon,
    required String title,
    required int count,
    required VoidCallback onClear,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppRadius.card),
        boxShadow: [AppShadows.elevation1],
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(AppSpacing.sm),
            decoration: BoxDecoration(
              color: AppColors.primary.withOpacity(0.1),
              borderRadius: BorderRadius.circular(AppRadius.button),
            ),
            child: Icon(icon, color: AppColors.primary),
          ),
          const SizedBox(width: AppSpacing.md),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: AppTextStyles.body1),
                const SizedBox(height: 2),
                Text(
                  '$count 条记录',
                  style: AppTextStyles.caption,
                ),
              ],
            ),
          ),
          TextButton(
            onPressed: count > 0 ? onClear : null,
            child: Text(
              '清除',
              style: TextStyle(
                color: count > 0 ? AppColors.error : AppColors.gray400,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildExportButton(BuildContext context, AppState appState) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppRadius.card),
        boxShadow: [AppShadows.elevation1],
      ),
      child: ListTile(
        leading: Container(
          padding: const EdgeInsets.all(AppSpacing.sm),
          decoration: BoxDecoration(
            color: AppColors.success.withOpacity(0.1),
            borderRadius: BorderRadius.circular(AppRadius.button),
          ),
          child: const Icon(Icons.download, color: AppColors.success),
        ),
        title: const Text('导出数据'),
        subtitle: const Text('将所有本地数据导出为JSON文件'),
        trailing: const Icon(Icons.chevron_right),
        onTap: () => _exportData(context, appState),
      ),
    );
  }

  Widget _buildDangerZone(BuildContext context, AppState appState) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: AppColors.error.withOpacity(0.05),
        borderRadius: BorderRadius.circular(AppRadius.card),
        border: Border.all(color: AppColors.error.withOpacity(0.3)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(Icons.warning_amber, color: AppColors.error, size: 20),
              const SizedBox(width: AppSpacing.xs),
              Text(
                '危险区域',
                style: AppTextStyles.body1.copyWith(color: AppColors.error),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () => _showClearDialog(
                context,
                '所有数据',
                appState.houses.length +
                    appState.schemes.length +
                    (appState.questionnaire != null ? 1 : 0),
                () async {
                  await appState.clearAllData();
                  if (context.mounted) {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('所有数据已清除')),
                    );
                  }
                },
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.error,
                foregroundColor: Colors.white,
              ),
              child: const Text('清除所有数据'),
            ),
          ),
        ],
      ),
    );
  }

  void _showClearDialog(
    BuildContext context,
    String dataName,
    int count,
    VoidCallback onConfirm,
  ) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认清除'),
        content: Text('确定要清除 $dataName吗？此操作不可恢复。'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              onConfirm();
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('$dataName 已清除')),
              );
            },
            style: TextButton.styleFrom(foregroundColor: AppColors.error),
            child: const Text('确认清除'),
          ),
        ],
      ),
    );
  }

  Future<void> _exportData(BuildContext context, AppState appState) async {
    try {
      final jsonData = await appState.exportData();
      showDialog(
        context: context,
        builder: (context) => AlertDialog(
          title: const Text('导出数据'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('数据已准备好，可以复制以下JSON内容：'),
                const SizedBox(height: AppSpacing.md),
                Container(
                  padding: const EdgeInsets.all(AppSpacing.sm),
                  decoration: BoxDecoration(
                    color: AppColors.gray100,
                    borderRadius: BorderRadius.circular(AppRadius.button),
                  ),
                  constraints: const BoxConstraints(maxHeight: 300),
                  child: SingleChildScrollView(
                    child: Text(
                      jsonData,
                      style: const TextStyle(
                        fontFamily: 'monospace',
                        fontSize: 10,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('关闭'),
            ),
          ],
        ),
      );
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('导出失败: $e')),
        );
      }
    }
  }

  Widget _buildLogUploadCard(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppRadius.card),
        boxShadow: [AppShadows.elevation1],
      ),
      child: ListTile(
        leading: Container(
          padding: const EdgeInsets.all(AppSpacing.sm),
          decoration: BoxDecoration(
            color: AppColors.warning.withOpacity(0.1),
            borderRadius: BorderRadius.circular(AppRadius.button),
          ),
          child: const Icon(Icons.upload_file, color: AppColors.warning),
        ),
        title: const Text('上传日志'),
        subtitle: const Text('将运行日志上传到服务器'),
        trailing: const Icon(Icons.chevron_right),
        onTap: () => _uploadLogs(context),
      ),
    );
  }

  Future<void> _uploadLogs(BuildContext context) async {
    final logger = LoggerService();
    final logs = await logger.getLogs();

    if (logs.isEmpty) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('暂无日志可上传')),
        );
      }
      return;
    }

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => const AlertDialog(
        content: Row(
          children: [
            CircularProgressIndicator(),
            SizedBox(width: 16),
            Text('正在上传日志...'),
          ],
        ),
      ),
    );

    final success = await logger.uploadLogs(
      onProgress: (current, total) {},
    );

    if (context.mounted) {
      Navigator.pop(context);

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success ? '日志上传成功' : '日志上传失败'),
          backgroundColor: success ? AppColors.success : AppColors.error,
        ),
      );
    }
  }
}
