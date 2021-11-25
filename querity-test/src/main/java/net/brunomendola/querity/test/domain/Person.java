package net.brunomendola.querity.test.domain;

import java.math.BigDecimal;
import java.util.List;

public interface Person<K extends Comparable<K>, A extends Address, L extends Location> {
  K getId();

  String getFirstName();

  void setFirstName(String firstName);

  String getLastName();

  void setLastName(String lastName);

  String getEmail();

  void setEmail(String email);

  Gender getGender();

  void setGender(Gender gender);

  java.time.LocalDate getBirthDate();

  void setBirthDate(java.time.LocalDate birthDate);

  BigDecimal getHeight();

  void setHeight(BigDecimal height);

  Integer getChildren();

  void setChildren(Integer children);

  boolean isMarried();

  void setMarried(boolean married);

  A getAddress();

  void setAddress(A address);

  List<L> getVisitedLocations();

  void setVisitedLocations(List<L> visitedLocations);

  enum Gender {
    M, F
  }
}
