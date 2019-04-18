package org.rit.swen440.dataLayer;

import lombok.Data;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A product category supported by the Order System
 */
@Data
public class Category {
  private String name;
  private String description;

  private Set<Product> products = new HashSet<>();

  public Optional<Product> findProduct(String name) {
    return products.stream()
        .filter(p -> p.getTitle().equalsIgnoreCase(name))
        .findFirst();
  }
}
