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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<Product> getProducts() {
    return products;
  }

  public void setProducts(Set<Product> products) {
    this.products = products;
  }
}
