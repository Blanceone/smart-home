import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/constants/app_theme.dart';
import '../../core/constants/api_constants.dart';
import '../../core/services/api_service.dart';
import '../../core/services/app_state.dart';
import '../../shared/models/house.dart';
import '../../shared/models/questionnaire.dart';
import '../../shared/models/scheme.dart';
import 'scheme_detail_page.dart';

class GeneratingPage extends StatefulWidget {
  final House house;
  final Questionnaire questionnaire;
  final UserPreference preferences;

  const GeneratingPage({
    Key? key,
    required this.house,
    required this.questionnaire,
    required this.preferences,
  }) : super(key: key);

  @override
  State<GeneratingPage> createState() => _GeneratingPageState();
}

class _GeneratingPageState extends State<GeneratingPage>
    with TickerProviderStateMixin {
  final ApiService _apiService = ApiService();

  int _currentPhase = 0;
  int _progress = 0;
  Timer? _progressTimer;
  String _currentTip = '';
  String? _taskId;

  final List<String> _phaseTips = [
    '正在上传您的数据...',
    'AI正在为您量身定制方案...',
    '正在匹配最适合您的产品...',
  ];

  final List<AnimationController> _pulseControllers = [];
  final List<Animation<double>> _pulseAnimations = [];

  @override
  void initState() {
    super.initState();
    _initAnimations();
    _startGeneration();
  }

  void _initAnimations() {
    for (int i = 0; i < 3; i++) {
      final controller = AnimationController(
        duration: Duration(milliseconds: 1500 + i * 500),
        vsync: this,
      );
      final animation = Tween<double>(begin: 1.0, end: 1.5).animate(
        CurvedAnimation(parent: controller, curve: Curves.easeOut),
      );
      _pulseControllers.add(controller);
      _pulseAnimations.add(animation);
      controller.repeat();
    }
  }

  void _startGeneration() async {
    _startProgressSimulation();

    final requestBody = {
      'house_layout': {
        'total_area': widget.house.totalArea,
        'rooms': widget.house.rooms.map((r) => {
          'room_name': r.roomName,
          'room_type': r.roomType,
          'length': r.length,
          'width': r.width,
          'area': r.area,
        }).toList(),
      },
      'questionnaire': widget.questionnaire.toJson(),
      'preferences': widget.preferences.toJson(),
    };

    final response = await _apiService.post(
      ApiConstants.schemesGenerate,
      body: requestBody,
    );

    if (response.success && response.data != null) {
      _taskId = response.data['task_id'];
      _pollTaskStatus();
    } else {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response.message)),
        );
        Navigator.of(context).pop();
      }
    }
  }

  void _startProgressSimulation() {
    _progressTimer = Timer.periodic(const Duration(milliseconds: 100), (timer) {
      if (_progress < 90) {
        setState(() {
          _progress += 1;
          _currentPhase = _progress < 30 ? 0 : (_progress < 70 ? 1 : 2);
          _currentTip = _phaseTips[_currentPhase];
        });
      }
    });
  }

  void _pollTaskStatus() async {
    if (_taskId == null) return;

    while (true) {
      await Future.delayed(const Duration(seconds: 2));

      final response = await _apiService.get('${ApiConstants.schemesTasks}/$_taskId');

      if (response.success && response.data != null) {
        final status = response.data['status'];

        if (status == 'success') {
          _progressTimer?.cancel();
          setState(() {
            _progress = 100;
            _currentPhase = 2;
            _currentTip = '方案生成完成！';
          });

          await Future.delayed(const Duration(milliseconds: 500));

          if (mounted) {
            final result = response.data['result'];
            if (result != null && result['scheme'] != null) {
              final scheme = Scheme.fromJson(result['scheme']);
              final appState = context.read<AppState>();
              await appState.addScheme(scheme);
              Navigator.of(context).pushReplacement(
                MaterialPageRoute(
                  builder: (_) => SchemeDetailPage(scheme: scheme),
                ),
              );
            } else {
              Navigator.of(context).pop();
            }
          }
          break;
        } else if (status == 'failed') {
          _progressTimer?.cancel();
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(response.data['error'] ?? '方案生成失败')),
            );
            Navigator.of(context).pop();
          }
          break;
        }
      }
    }
  }

  @override
  void dispose() {
    _progressTimer?.cancel();
    for (var controller in _pulseControllers) {
      controller.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.gray900,
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    SizedBox(
                      width: 200,
                      height: 200,
                      child: Stack(
                        alignment: Alignment.center,
                        children: [
                          ...List.generate(3, (index) {
                            return AnimatedBuilder(
                              animation: _pulseAnimations[index],
                              builder: (context, child) {
                                return Container(
                                  width: 100 * _pulseAnimations[index].value,
                                  height: 100 * _pulseAnimations[index].value,
                                  decoration: BoxDecoration(
                                    shape: BoxShape.circle,
                                    color: AppColors.primary
                                        .withOpacity(0.1 * (3 - index) / 3),
                                  ),
                                );
                              },
                            );
                          }),
                          Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                '$_progress%',
                                style: AppTextStyles.h1.copyWith(
                                  color: Colors.white,
                                  fontSize: 48,
                                ),
                              ),
                              Text(
                                _currentTip,
                                style: AppTextStyles.body2.copyWith(
                                  color: Colors.white70,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: AppSpacing.xl),
                    _buildPhaseIndicator(),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPhaseIndicator() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: AppSpacing.xl),
      child: Column(
        children: [
          Row(
            children: [
              _buildPhaseDot(0, '数据分析'),
              _buildPhaseLine(0),
              _buildPhaseDot(1, '方案生成'),
              _buildPhaseLine(1),
              _buildPhaseDot(2, '产品匹配'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildPhaseDot(int phase, String label) {
    final isActive = _currentPhase >= phase;

    return Column(
      children: [
        Container(
          width: 12,
          height: 12,
          decoration: BoxDecoration(
            color: isActive ? AppColors.primary : AppColors.gray600,
            shape: BoxShape.circle,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: AppTextStyles.caption.copyWith(
            color: isActive ? AppColors.primary : AppColors.gray600,
          ),
        ),
      ],
    );
  }

  Widget _buildPhaseLine(int phase) {
    return Expanded(
      child: Container(
        height: 2,
        margin: const EdgeInsets.only(bottom: 16),
        color: _currentPhase > phase ? AppColors.primary : AppColors.gray600,
      ),
    );
  }
}
