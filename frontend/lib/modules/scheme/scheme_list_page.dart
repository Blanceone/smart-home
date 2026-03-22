import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/services/app_state.dart';
import '../../core/constants/app_theme.dart';
import '../../shared/models/scheme.dart';
import 'scheme_detail_page.dart';

class SchemeListPage extends StatelessWidget {
  const SchemeListPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.gray100,
      appBar: AppBar(
        title: const Text('我的方案'),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.gray900,
        elevation: 0,
      ),
      body: Consumer<AppState>(
        builder: (context, appState, child) {
          final schemes = appState.schemes;

          if (schemes.isEmpty) {
            return _buildEmptyState();
          }

          return ListView.builder(
            padding: const EdgeInsets.all(AppSpacing.md),
            itemCount: schemes.length,
            itemBuilder: (context, index) {
              final scheme = schemes[index];
              return _buildSchemeCard(context, scheme);
            },
          );
        },
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.lightbulb_outline,
            size: 80,
            color: AppColors.gray300,
          ),
          const SizedBox(height: AppSpacing.md),
          Text(
            '暂无方案',
            style: AppTextStyles.h3.copyWith(color: AppColors.gray500),
          ),
          const SizedBox(height: AppSpacing.sm),
          Text(
            '开始创建您的第一个智能家居方案',
            style: AppTextStyles.body2,
          ),
        ],
      ),
    );
  }

  Widget _buildSchemeCard(BuildContext context, Scheme scheme) {
    return GestureDetector(
      onTap: () async {
        await Navigator.of(context).push(
          MaterialPageRoute(
            builder: (_) => SchemeDetailPage(scheme: scheme),
          ),
        );
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: AppSpacing.md),
        padding: const EdgeInsets.all(AppSpacing.md),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(AppRadius.card),
          boxShadow: [AppShadows.elevation1],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    scheme.schemeName,
                    style: AppTextStyles.h4,
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppSpacing.sm,
                    vertical: 2,
                  ),
                  decoration: BoxDecoration(
                    color: scheme.status == 2
                        ? AppColors.success.withOpacity(0.1)
                        : AppColors.warning.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(AppRadius.tag),
                  ),
                  child: Text(
                    scheme.status == 2 ? '已完成' : '生成中',
                    style: AppTextStyles.caption.copyWith(
                      color: scheme.status == 2 ? AppColors.success : AppColors.warning,
                    ),
                  ),
                ),
              ],
            ),
            if (scheme.description != null && scheme.description!.isNotEmpty) ...[
              const SizedBox(height: AppSpacing.xs),
              Text(
                scheme.description!,
                style: AppTextStyles.body2.copyWith(color: AppColors.gray600),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
            ],
            const SizedBox(height: AppSpacing.md),
            Row(
              children: [
                _buildInfoChip(Icons.devices, '${scheme.devices.length} 件设备'),
                const SizedBox(width: AppSpacing.md),
                _buildInfoChip(Icons.attach_money, '¥${scheme.totalPrice.toStringAsFixed(2)}'),
                const Spacer(),
                Text(
                  _formatDate(scheme.createdAt),
                  style: AppTextStyles.caption,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoChip(IconData icon, String text) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 14, color: AppColors.gray500),
        const SizedBox(width: 4),
        Text(text, style: AppTextStyles.caption),
      ],
    );
  }

  String _formatDate(DateTime date) {
    return '${date.month}/${date.day} ${date.hour}:${date.minute.toString().padLeft(2, '0')}';
  }
}
