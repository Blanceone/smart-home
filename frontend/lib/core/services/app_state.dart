import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import '../../shared/models/house.dart';
import '../../shared/models/scheme.dart';
import '../../shared/models/questionnaire.dart';

class AppState extends ChangeNotifier {
  static const String _housesKey = 'local_houses';
  static const String _schemesKey = 'local_schemes';
  static const String _questionnaireKey = 'local_questionnaire';
  static const String _preferencesKey = 'local_preferences';

  List<House> _houses = [];
  List<Scheme> _schemes = [];
  Questionnaire? _questionnaire;
  UserPreference? _preferences;

  List<House> get houses => _houses;
  List<Scheme> get schemes => _schemes;
  Questionnaire? get questionnaire => _questionnaire;
  UserPreference? get preferences => _preferences;

  House? currentHouse;
  Scheme? currentScheme;

  Future<void> init() async {
    await _loadHouses();
    await _loadSchemes();
    await _loadQuestionnaire();
    await _loadPreferences();
    notifyListeners();
  }

  Future<void> _loadHouses() async {
    final prefs = await SharedPreferences.getInstance();
    final data = prefs.getString(_housesKey);
    if (data != null) {
      final List<dynamic> jsonList = jsonDecode(data);
      _houses = jsonList.map((json) => House.fromJson(json)).toList();
    }
  }

  Future<void> _saveHouses() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonList = _houses.map((h) => h.toJson()).toList();
    await prefs.setString(_housesKey, jsonEncode(jsonList));
  }

  Future<void> _loadSchemes() async {
    final prefs = await SharedPreferences.getInstance();
    final data = prefs.getString(_schemesKey);
    if (data != null) {
      final List<dynamic> jsonList = jsonDecode(data);
      _schemes = jsonList.map((json) => Scheme.fromJson(json)).toList();
    }
  }

  Future<void> _saveSchemes() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonList = _schemes.map((s) => s.toJson()).toList();
    await prefs.setString(_schemesKey, jsonEncode(jsonList));
  }

  Future<void> _loadQuestionnaire() async {
    final prefs = await SharedPreferences.getInstance();
    final data = prefs.getString(_questionnaireKey);
    if (data != null) {
      _questionnaire = Questionnaire.fromJson(jsonDecode(data));
    }
  }

  Future<void> _saveQuestionnaire() async {
    final prefs = await SharedPreferences.getInstance();
    if (_questionnaire != null) {
      await prefs.setString(_questionnaireKey, jsonEncode(_questionnaire!.toJson()));
    }
  }

  Future<void> _loadPreferences() async {
    final prefs = await SharedPreferences.getInstance();
    final data = prefs.getString(_preferencesKey);
    if (data != null) {
      _preferences = UserPreference.fromJson(jsonDecode(data));
    }
  }

  Future<void> _savePreferences() async {
    final prefs = await SharedPreferences.getInstance();
    if (_preferences != null) {
      await prefs.setString(_preferencesKey, jsonEncode(_preferences!.toJson()));
    }
  }

  Future<void> addHouse(House house) async {
    _houses.add(house);
    await _saveHouses();
    notifyListeners();
  }

  Future<void> updateHouse(House house) async {
    final index = _houses.indexWhere((h) => h.id == house.id);
    if (index >= 0) {
      _houses[index] = house;
      await _saveHouses();
      notifyListeners();
    }
  }

  Future<void> deleteHouse(String houseId) async {
    _houses.removeWhere((h) => h.id == houseId);
    await _saveHouses();
    notifyListeners();
  }

  void setCurrentHouse(House house) {
    currentHouse = house;
    notifyListeners();
  }

  Future<void> saveQuestionnaire(Questionnaire questionnaire) async {
    _questionnaire = questionnaire;
    await _saveQuestionnaire();
    notifyListeners();
  }

  Future<void> savePreferences(UserPreference preferences) async {
    _preferences = preferences;
    await _savePreferences();
    notifyListeners();
  }

  Future<void> addScheme(Scheme scheme) async {
    _schemes.insert(0, scheme);
    await _saveSchemes();
    notifyListeners();
  }

  Future<void> updateScheme(Scheme scheme) async {
    final index = _schemes.indexWhere((s) => s.id == scheme.id);
    if (index >= 0) {
      _schemes[index] = scheme;
      await _saveSchemes();
      notifyListeners();
    }
  }

  void setCurrentScheme(Scheme scheme) {
    currentScheme = scheme;
    notifyListeners();
  }

  Future<void> clearAllData() async {
    _houses.clear();
    _schemes.clear();
    _questionnaire = null;
    _preferences = null;

    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_housesKey);
    await prefs.remove(_schemesKey);
    await prefs.remove(_questionnaireKey);
    await prefs.remove(_preferencesKey);

    notifyListeners();
  }

  Future<String> exportData() async {
    final data = {
      'houses': _houses.map((h) => h.toJson()).toList(),
      'schemes': _schemes.map((s) => s.toJson()).toList(),
      'questionnaire': _questionnaire?.toJson(),
      'preferences': _preferences?.toJson(),
      'exportTime': DateTime.now().toIso8601String(),
    };
    return jsonEncode(data);
  }
}
