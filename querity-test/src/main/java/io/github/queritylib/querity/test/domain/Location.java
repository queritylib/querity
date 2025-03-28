package io.github.queritylib.querity.test.domain;

import java.util.List;

public interface Location {
  List<String> getCities();

  void setCities(List<String> city);

  String getCountry();

  void setCountry(String country);
}
