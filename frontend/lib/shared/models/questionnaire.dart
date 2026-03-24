class Questionnaire {
  String livingStatus;
  int residentCount;
  bool hasElderly;
  bool hasChildren;
  bool hasPets;
  List<String> preferredScenarios;
  String? sleepPattern;
  String? knowledgeLevel;

  Questionnaire({
    this.livingStatus = 'own',
    this.residentCount = 1,
    this.hasElderly = false,
    this.hasChildren = false,
    this.hasPets = false,
    this.preferredScenarios = const [],
    this.sleepPattern,
    this.knowledgeLevel,
  });

  factory Questionnaire.fromJson(Map<String, dynamic> json) {
    return Questionnaire(
      livingStatus: json['living_status'] ?? json['livingStatus'] ?? 'own',
      residentCount: json['resident_count'] ?? json['residentCount'] ?? 1,
      hasElderly: json['has_elderly'] ?? json['hasElderly'] ?? false,
      hasChildren: json['has_children'] ?? json['hasChildren'] ?? false,
      hasPets: json['has_pets'] ?? json['hasPets'] ?? false,
      preferredScenarios: json['preferred_scenarios'] != null
          ? List<String>.from(json['preferred_scenarios'])
          : json['preferredScenarios'] != null
              ? List<String>.from(json['preferredScenarios'])
              : [],
      sleepPattern: json['sleep_pattern'] ?? json['sleepPattern'],
      knowledgeLevel: json['knowledge_level'] ?? json['knowledgeLevel'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'living_status': livingStatus,
      'resident_count': residentCount,
      'has_elderly': hasElderly,
      'has_children': hasChildren,
      'has_pets': hasPets,
      'preferred_scenarios': preferredScenarios,
      'sleep_pattern': sleepPattern,
      'knowledge_level': knowledgeLevel,
    };
  }
}

class UserPreference {
  double budgetMin;
  double budgetMax;
  List<String> preferredBrands;
  List<String> excludedBrands;

  UserPreference({
    this.budgetMin = 0,
    this.budgetMax = 100000,
    this.preferredBrands = const [],
    this.excludedBrands = const [],
  });

  factory UserPreference.fromJson(Map<String, dynamic> json) {
    return UserPreference(
      budgetMin: (json['budget_min'] ?? json['budgetMin'] ?? 0).toDouble(),
      budgetMax: (json['budget_max'] ?? json['budgetMax'] ?? 100000).toDouble(),
      preferredBrands: json['preferred_brands'] != null
          ? List<String>.from(json['preferred_brands'])
          : json['preferredBrands'] != null
              ? List<String>.from(json['preferredBrands'])
              : [],
      excludedBrands: json['excluded_brands'] != null
          ? List<String>.from(json['excluded_brands'])
          : json['excludedBrands'] != null
              ? List<String>.from(json['excludedBrands'])
              : [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'budget_min': budgetMin,
      'budget_max': budgetMax,
      'preferred_brands': preferredBrands,
      'excluded_brands': excludedBrands,
    };
  }
}

class QuestionnaireQuestion {
  final String questionId;
  final String questionText;
  final String? subQuestion;
  final List<QuestionOption> options;

  QuestionnaireQuestion({
    required this.questionId,
    required this.questionText,
    this.subQuestion,
    this.options = const [],
  });

  static List<QuestionnaireQuestion> getDefaultQuestions() {
    return [
      QuestionnaireQuestion(
        questionId: 'living_status',
        questionText: '您的居住情况是？',
        options: [
          QuestionOption(value: 'own', label: '业主自住'),
          QuestionOption(value: 'rent', label: '租户居住'),
          QuestionOption(value: 'other', label: '其他'),
        ],
      ),
      QuestionnaireQuestion(
        questionId: 'resident_count',
        questionText: '居住人口数量？',
        options: [
          QuestionOption(value: '1', label: '1人'),
          QuestionOption(value: '2', label: '2人'),
          QuestionOption(value: '3', label: '3-4人'),
          QuestionOption(value: '5', label: '5人以上'),
        ],
      ),
      QuestionnaireQuestion(
        questionId: 'has_elderly',
        questionText: '是否有老年人同住？',
        options: [
          QuestionOption(value: 'true', label: '是'),
          QuestionOption(value: 'false', label: '否'),
        ],
      ),
      QuestionnaireQuestion(
        questionId: 'has_children',
        questionText: '是否有儿童同住？',
        options: [
          QuestionOption(value: 'true', label: '是'),
          QuestionOption(value: 'false', label: '否'),
        ],
      ),
      QuestionnaireQuestion(
        questionId: 'has_pets',
        questionText: '是否养宠物？',
        options: [
          QuestionOption(value: 'true', label: '是'),
          QuestionOption(value: 'false', label: '否'),
        ],
      ),
      QuestionnaireQuestion(
        questionId: 'preferred_scenarios',
        questionText: '您最看重哪些智能场景？',
        options: [
          QuestionOption(value: 'security', label: '安全防护'),
          QuestionOption(value: 'comfort', label: '舒适便捷'),
          QuestionOption(value: 'energy', label: '节能省电'),
          QuestionOption(value: 'entertainment', label: '娱乐影音'),
        ],
      ),
      QuestionnaireQuestion(
        questionId: 'sleep_pattern',
        questionText: '您的作息规律是？',
        options: [
          QuestionOption(value: 'early', label: '早睡早起'),
          QuestionOption(value: 'normal', label: '正常作息'),
          QuestionOption(value: 'late', label: '晚睡晚起'),
          QuestionOption(value: 'irregular', label: '不规律'),
        ],
      ),
      QuestionnaireQuestion(
        questionId: 'knowledge_level',
        questionText: '您对智能家居的了解程度？',
        options: [
          QuestionOption(value: 'none', label: '完全不了解'),
          QuestionOption(value: 'basic', label: '基本了解'),
          QuestionOption(value: 'familiar', label: '比较熟悉'),
          QuestionOption(value: 'expert', label: '专业人士'),
        ],
      ),
    ];
  }
}

class QuestionOption {
  final String value;
  final String label;
  final String? icon;

  QuestionOption({
    required this.value,
    required this.label,
    this.icon,
  });
}
