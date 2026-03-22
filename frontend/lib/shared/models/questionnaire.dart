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
