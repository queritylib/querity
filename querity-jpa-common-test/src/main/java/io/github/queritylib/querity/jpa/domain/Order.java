package io.github.queritylib.querity.jpa.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`order`")
public class Order extends AbstractPersistable<Long> implements io.github.queritylib.querity.test.domain.Order<OrderItem> {
  @ManyToOne
  private Person person;
  @NonNull
  private Short year;
  @NonNull
  private Integer number;
  @NonNull
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();
  private UUID externalId;
}
