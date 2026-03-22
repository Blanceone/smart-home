import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/constants/app_theme.dart';
import '../../shared/models/scheme.dart';
import 'package:url_launcher/url_launcher.dart';

class SchemeDetailPage extends StatelessWidget {
  final Scheme scheme;

  const SchemeDetailPage({Key? key, required this.scheme}) : super(key: key);

  Map<String, List<SchemeDevice>> get _devicesByRoom {
    final Map<String, List<SchemeDevice>> grouped = {};
    for (var device in scheme.devices) {
      if (!grouped.containsKey(device.roomName)) {
        grouped[device.roomName] = [];
      }
      grouped[device.roomName]!.add(device);
    }
    return grouped;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.gray100,
      appBar: AppBar(
        title: Text(scheme.schemeName),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.gray900,
        elevation: 0,
      ),
      body: Column(
        children: [
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(AppSpacing.md),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildHeader(context),
                  const SizedBox(height: AppSpacing.md),
                  if (scheme.description != null && scheme.description!.isNotEmpty)
                    _buildDescription(context),
                  const SizedBox(height: AppSpacing.md),
                  ..._buildRoomSections(context),
                ],
              ),
            ),
          ),
          _buildBottomBar(context),
        ],
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.lg),
      decoration: BoxDecoration(
        color: AppColors.primary,
        borderRadius: BorderRadius.circular(AppRadius.card),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            scheme.schemeName,
            style: AppTextStyles.h3.copyWith(color: Colors.white),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              _buildStatItem(Icons.devices, '${scheme.devices.length} 件设备'),
              const SizedBox(width: AppSpacing.lg),
              _buildStatItem(Icons.attach_money, '¥${scheme.totalPrice.toStringAsFixed(2)}'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatItem(IconData icon, String text) {
    return Row(
      children: [
        Icon(icon, color: Colors.white70, size: 16),
        const SizedBox(width: 4),
        Text(text, style: AppTextStyles.body2.copyWith(color: Colors.white)),
      ],
    );
  }

  Widget _buildDescription(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppRadius.card),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('方案描述', style: AppTextStyles.h4),
          const SizedBox(height: AppSpacing.sm),
          Text(
            scheme.description!,
            style: AppTextStyles.body2.copyWith(color: AppColors.gray600),
          ),
        ],
      ),
    );
  }

  List<Widget> _buildRoomSections(BuildContext context) {
    final rooms = _devicesByRoom.keys.toList();
    return rooms.map((roomName) {
      final devices = _devicesByRoom[roomName]!;
      return Container(
        margin: const EdgeInsets.only(bottom: AppSpacing.md),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(AppRadius.card),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.all(AppSpacing.md),
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(AppSpacing.sm),
                    decoration: BoxDecoration(
                      color: AppColors.primary.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(AppRadius.button),
                    ),
                    child: const Icon(Icons.meeting_room, color: AppColors.primary, size: 20),
                  ),
                  const SizedBox(width: AppSpacing.sm),
                  Text(roomName, style: AppTextStyles.h4),
                  const Spacer(),
                  Text(
                    '${devices.length} 件设备',
                    style: AppTextStyles.caption,
                  ),
                ],
              ),
            ),
            const Divider(height: 1),
            ...devices.map((device) => _buildDeviceItem(context, device)),
          ],
        ),
      );
    }).toList();
  }

  Widget _buildDeviceItem(BuildContext context, SchemeDevice device) {
    return InkWell(
      onTap: () => _showDeviceDetail(context, device),
      child: Container(
        padding: const EdgeInsets.all(AppSpacing.md),
        child: Row(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(AppRadius.button),
              child: device.imageUrl != null && device.imageUrl!.isNotEmpty
                  ? CachedNetworkImage(
                      imageUrl: device.imageUrl!,
                      width: 60,
                      height: 60,
                      fit: BoxFit.cover,
                      placeholder: (_, __) => Container(
                        width: 60,
                        height: 60,
                        color: AppColors.gray200,
                        child: const Icon(Icons.image, color: AppColors.gray500),
                      ),
                      errorWidget: (_, __, ___) => Container(
                        width: 60,
                        height: 60,
                        color: AppColors.gray200,
                        child: const Icon(Icons.image, color: AppColors.gray500),
                      ),
                    )
                  : Container(
                      width: 60,
                      height: 60,
                      color: AppColors.gray200,
                      child: const Icon(Icons.image, color: AppColors.gray500),
                    ),
            ),
            const SizedBox(width: AppSpacing.md),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    device.productName,
                    style: AppTextStyles.body1.copyWith(fontWeight: FontWeight.w600),
                  ),
                  if (device.brandName != null) ...[
                    const SizedBox(height: 2),
                    Text('品牌: ${device.brandName}', style: AppTextStyles.caption),
                  ],
                  const SizedBox(height: 2),
                  Text(
                    '¥${device.unitPrice.toStringAsFixed(2)} × ${device.quantity}',
                    style: AppTextStyles.body2,
                  ),
                ],
              ),
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  '¥${device.subtotal.toStringAsFixed(2)}',
                  style: AppTextStyles.body1.copyWith(
                    color: AppColors.primary,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                if (device.productUrl != null) ...[
                  const SizedBox(height: AppSpacing.xs),
                  GestureDetector(
                    onTap: () => _launchUrl(device.productUrl!),
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: AppSpacing.sm,
                        vertical: 2,
                      ),
                      decoration: BoxDecoration(
                        color: AppColors.success.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(AppRadius.tag),
                      ),
                      child: Text(
                        '去购买',
                        style: AppTextStyles.caption.copyWith(color: AppColors.success),
                      ),
                    ),
                  ),
                ],
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBottomBar(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [AppShadows.elevation2],
      ),
      child: SafeArea(
        child: Row(
          children: [
            Expanded(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('预估总价', style: AppTextStyles.caption),
                  Text(
                    '¥${scheme.totalPrice.toStringAsFixed(2)}',
                    style: AppTextStyles.h3.copyWith(color: AppColors.primary),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showDeviceDetail(BuildContext context, SchemeDevice device) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) => Container(
        padding: const EdgeInsets.all(AppSpacing.lg),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(device.productName, style: AppTextStyles.h3),
            if (device.brandName != null) ...[
              const SizedBox(height: AppSpacing.xs),
              Text('品牌: ${device.brandName}', style: AppTextStyles.caption),
            ],
            const SizedBox(height: AppSpacing.md),
            if (device.reason != null) ...[
              Text('推荐理由', style: AppTextStyles.h4),
              const SizedBox(height: AppSpacing.xs),
              Text(device.reason!, style: AppTextStyles.body2),
            ],
            const SizedBox(height: AppSpacing.lg),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '¥${device.subtotal.toStringAsFixed(2)}',
                  style: AppTextStyles.h3.copyWith(color: AppColors.primary),
                ),
                if (device.productUrl != null)
                  ElevatedButton(
                    onPressed: () => _launchUrl(device.productUrl!),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.success,
                    ),
                    child: const Text('去购买'),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _launchUrl(String url) async {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    }
  }
}
