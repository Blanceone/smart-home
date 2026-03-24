import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/constants/app_theme.dart';
import '../../core/constants/api_constants.dart';
import '../../core/services/api_service.dart';
import '../../core/services/app_state.dart';
import '../../shared/models/house.dart';
import '../../shared/models/questionnaire.dart';
import '../../shared/models/product.dart';
import '../../shared/widgets/app_button.dart';
import '../scheme/generating_page.dart';

class QuestionnairePage extends StatefulWidget {
  final House house;

  const QuestionnairePage({Key? key, required this.house}) : super(key: key);

  @override
  State<QuestionnairePage> createState() => _QuestionnairePageState();
}

class _QuestionnairePageState extends State<QuestionnairePage> {
  final ApiService _apiService = ApiService();

  final List<QuestionnaireQuestion> _questions = QuestionnaireQuestion.getDefaultQuestions();
  final Map<String, dynamic> _answers = {};

  int _currentStep = 0;
  bool _isLoading = false;
  List<Brand> _brands = [];
  UserPreference _preferences = UserPreference();

  @override
  void initState() {
    super.initState();
    _loadBrands();
    _initAnswers();
  }

  void _initAnswers() {
    _answers['living_status'] = 'own';
    _answers['resident_count'] = '1';
    _answers['has_elderly'] = false;
    _answers['has_children'] = false;
    _answers['has_pets'] = false;
    _answers['preferred_scenarios'] = <String>[];
    _answers['sleep_pattern'] = 'normal';
    _answers['knowledge_level'] = 'basic';
  }

  Future<void> _loadBrands() async {
    final response = await _apiService.get(ApiConstants.brands);
    if (response.success && response.data != null) {
      setState(() {
        _brands = (response.data as List).map((b) => Brand.fromJson(b)).toList();
      });
    }
  }

  void _nextStep() {
    if (_currentStep < 2) {
      setState(() => _currentStep++);
    } else {
      _submit();
    }
  }

  Future<void> _submit() async {
    setState(() => _isLoading = true);

    final questionnaire = Questionnaire(
      livingStatus: _answers['living_status'] ?? 'own',
      residentCount: int.tryParse(_answers['resident_count']?.toString() ?? '1') ?? 1,
      hasElderly: _answers['has_elderly'] ?? false,
      hasChildren: _answers['has_children'] ?? false,
      hasPets: _answers['has_pets'] ?? false,
      preferredScenarios: List<String>.from(_answers['preferred_scenarios'] ?? []),
      sleepPattern: _answers['sleep_pattern'],
      knowledgeLevel: _answers['knowledge_level'],
    );

    final appState = context.read<AppState>();
    await appState.saveQuestionnaire(questionnaire);
    await appState.savePreferences(_preferences);

    if (mounted) {
      setState(() => _isLoading = false);

      Navigator.of(context).pushReplacement(
        MaterialPageRoute(
          builder: (_) => GeneratingPage(
            house: widget.house,
            questionnaire: questionnaire,
            preferences: _preferences,
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.gray100,
      appBar: AppBar(
        title: const Text('问卷调查'),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.gray900,
        elevation: 0,
      ),
      body: Column(
        children: [
          _buildProgressIndicator(),
          Expanded(
            child: IndexedStack(
              index: _currentStep,
              children: [
                _buildBasicInfoStep(),
                _buildScenarioStep(),
                _buildPreferenceStep(),
              ],
            ),
          ),
          _buildBottomBar(),
        ],
      ),
    );
  }

  Widget _buildProgressIndicator() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      color: Colors.white,
      child: Row(
        children: [
          _buildStepDot(0, '基本信息'),
          _buildStepLine(0),
          _buildStepDot(1, '智能场景'),
          _buildStepLine(1),
          _buildStepDot(2, '预算偏好'),
        ],
      ),
    );
  }

  Widget _buildStepDot(int step, String label) {
    final isActive = _currentStep >= step;
    final isCurrent = _currentStep == step;

    return Column(
      children: [
        Container(
          width: 28,
          height: 28,
          decoration: BoxDecoration(
            color: isActive ? AppColors.primary : AppColors.gray200,
            shape: BoxShape.circle,
          ),
          child: Center(
            child: isActive && !isCurrent
                ? const Icon(Icons.check, color: Colors.white, size: 16)
                : Text(
                    '${step + 1}',
                    style: TextStyle(
                      color: isActive ? Colors.white : AppColors.gray500,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: AppTextStyles.caption.copyWith(
            color: isActive ? AppColors.primary : AppColors.gray500,
          ),
        ),
      ],
    );
  }

  Widget _buildStepLine(int step) {
    return Expanded(
      child: Container(
        height: 2,
        margin: const EdgeInsets.only(bottom: 20),
        color: _currentStep > step ? AppColors.primary : AppColors.gray200,
      ),
    );
  }

  Widget _buildBasicInfoStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.md),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildQuestionCard(
            '您的居住情况是？',
            [
              _buildOptionTile('自有住房', 'own', 'living_status'),
              _buildOptionTile('租房', 'rent', 'living_status'),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          _buildQuestionCard(
            '您家里常住几人？',
            [
              _buildOptionTile('1人', '1', 'resident_count'),
              _buildOptionTile('2人', '2', 'resident_count'),
              _buildOptionTile('3人', '3', 'resident_count'),
              _buildOptionTile('4人及以上', '4+', 'resident_count'),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          _buildQuestionCard(
            '家中是否有老人？',
            [
              _buildOptionTile('是', true, 'has_elderly'),
              _buildOptionTile('否', false, 'has_elderly'),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          _buildQuestionCard(
            '家中是否有儿童？',
            [
              _buildOptionTile('是', true, 'has_children'),
              _buildOptionTile('否', false, 'has_children'),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          _buildQuestionCard(
            '家中是否养宠物？',
            [
              _buildOptionTile('是', true, 'has_pets'),
              _buildOptionTile('否', false, 'has_pets'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildScenarioStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.md),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('您最希望实现哪些智能场景？（可多选）', style: AppTextStyles.h4),
          const SizedBox(height: AppSpacing.md),
          GridView.count(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            crossAxisCount: 2,
            mainAxisSpacing: AppSpacing.md,
            crossAxisSpacing: AppSpacing.md,
            childAspectRatio: 2.5,
            children: [
              _buildScenarioTile('智能照明', Icons.lightbulb, AppColors.lighting, 'lighting'),
              _buildScenarioTile('智能安防', Icons.security, AppColors.security, 'security'),
              _buildScenarioTile('智能窗帘', Icons.curtains, AppColors.curtain, 'curtain'),
              _buildScenarioTile('智能家电', Icons.kitchen, AppColors.appliance, 'appliance'),
              _buildScenarioTile('智能环境', Icons.air, AppColors.environment, 'environment'),
              _buildScenarioTile('智能影音', Icons.tv, AppColors.audio, 'audio'),
            ],
          ),
          const SizedBox(height: AppSpacing.lg),
          _buildQuestionCard(
            '您平时的作息习惯是？',
            [
              _buildOptionTile('早睡早起', 'early', 'sleep_pattern'),
              _buildOptionTile('晚睡晚起', 'late', 'sleep_pattern'),
              _buildOptionTile('作息不规律', 'irregular', 'sleep_pattern'),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          _buildQuestionCard(
            '您对智能家居的了解程度？',
            [
              _buildOptionTile('完全不了解', 'none', 'knowledge_level'),
              _buildOptionTile('了解一些', 'basic', 'knowledge_level'),
              _buildOptionTile('比较熟悉', 'familiar', 'knowledge_level'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildPreferenceStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.md),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('您的预算范围？', style: AppTextStyles.h4),
          const SizedBox(height: AppSpacing.md),
          Row(
            children: [
              Expanded(
                child: TextFormField(
                  initialValue: _preferences.budgetMin?.toStringAsFixed(0) ?? '5000',
                  decoration: InputDecoration(
                    labelText: '最低预算',
                    prefixText: '¥ ',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(AppRadius.button),
                    ),
                  ),
                  keyboardType: TextInputType.number,
                  onChanged: (value) {
                    _preferences.budgetMin = double.tryParse(value) ?? 0.0;
                  },
                ),
              ),
              const SizedBox(width: AppSpacing.md),
              Expanded(
                child: TextFormField(
                  initialValue: _preferences.budgetMax?.toStringAsFixed(0) ?? '50000',
                  decoration: InputDecoration(
                    labelText: '最高预算',
                    prefixText: '¥ ',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(AppRadius.button),
                    ),
                  ),
                  keyboardType: TextInputType.number,
                  onChanged: (value) {
                    _preferences.budgetMax = double.tryParse(value) ?? 0.0;
                  },
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.lg),
          Text('品牌偏好', style: AppTextStyles.h4),
          const SizedBox(height: AppSpacing.sm),
          Text(
            '选择您喜欢的品牌（可多选）',
            style: AppTextStyles.body2,
          ),
          const SizedBox(height: AppSpacing.md),
          if (_brands.isEmpty)
            const Center(child: CircularProgressIndicator())
          else
            Wrap(
              spacing: AppSpacing.sm,
              runSpacing: AppSpacing.sm,
              children: _brands.map((brand) {
                final isSelected = _preferences.preferredBrands?.contains(brand.brandName) ?? false;
                return FilterChip(
                  label: Text(brand.brandName),
                  selected: isSelected,
                  onSelected: (selected) {
                    setState(() {
                      _preferences.preferredBrands ??= [];
                      if (selected) {
                        _preferences.preferredBrands!.add(brand.brandName);
                      } else {
                        _preferences.preferredBrands!.remove(brand.brandName);
                      }
                    });
                  },
                  selectedColor: AppColors.primary.withOpacity(0.2),
                  checkmarkColor: AppColors.primary,
                );
              }).toList(),
            ),
          const SizedBox(height: AppSpacing.lg),
          Text('排除品牌', style: AppTextStyles.h4),
          const SizedBox(height: AppSpacing.sm),
          Text(
            '选择您不想要的品牌（可多选）',
            style: AppTextStyles.body2,
          ),
          const SizedBox(height: AppSpacing.md),
          if (_brands.isEmpty)
            const Center(child: CircularProgressIndicator())
          else
            Wrap(
              spacing: AppSpacing.sm,
              runSpacing: AppSpacing.sm,
              children: _brands.map((brand) {
                final isSelected = _preferences.excludedBrands?.contains(brand.brandName) ?? false;
                return FilterChip(
                  label: Text(brand.brandName),
                  selected: isSelected,
                  onSelected: (selected) {
                    setState(() {
                      _preferences.excludedBrands ??= [];
                      if (selected) {
                        _preferences.excludedBrands!.add(brand.brandName);
                      } else {
                        _preferences.excludedBrands!.remove(brand.brandName);
                      }
                    });
                  },
                  selectedColor: AppColors.error.withOpacity(0.2),
                  checkmarkColor: AppColors.error,
                );
              }).toList(),
            ),
        ],
      ),
    );
  }

  Widget _buildQuestionCard(String question, List<Widget> options) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AppRadius.card),
        boxShadow: [AppShadows.elevation1],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(question, style: AppTextStyles.body1),
          const SizedBox(height: AppSpacing.sm),
          ...options,
        ],
      ),
    );
  }

  Widget _buildOptionTile(String label, dynamic value, String key) {
    final isSelected = _answers[key] == value;

    return RadioListTile<dynamic>(
      title: Text(label),
      value: value,
      groupValue: _answers[key],
      onChanged: (v) {
        setState(() {
          _answers[key] = v;
        });
      },
      activeColor: AppColors.primary,
      contentPadding: EdgeInsets.zero,
      dense: true,
    );
  }

  Widget _buildScenarioTile(String label, IconData icon, Color color, String key) {
    final isSelected = (_answers['preferred_scenarios'] as List<String>).contains(key);

    return GestureDetector(
      onTap: () {
        setState(() {
          final list = _answers['preferred_scenarios'] as List<String>;
          if (isSelected) {
            list.remove(key);
          } else {
            list.add(key);
          }
        });
      },
      child: Container(
        decoration: BoxDecoration(
          color: isSelected ? color.withOpacity(0.1) : Colors.white,
          borderRadius: BorderRadius.circular(AppRadius.card),
          border: Border.all(
            color: isSelected ? color : AppColors.gray200,
            width: isSelected ? 2 : 1,
          ),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: isSelected ? color : AppColors.gray500),
            const SizedBox(width: AppSpacing.xs),
            Text(
              label,
              style: AppTextStyles.body2.copyWith(
                color: isSelected ? color : AppColors.gray600,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBottomBar() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [AppShadows.elevation2],
      ),
      child: SafeArea(
        child: AppButton(
          text: _currentStep == 2 ? '开始生成' : '下一步',
          isLoading: _isLoading,
          onPressed: _nextStep,
        ),
      ),
    );
  }
}
