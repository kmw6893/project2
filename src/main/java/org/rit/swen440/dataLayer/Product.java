package org.rit.swen440.dataLayer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.nio.file.Path;

/**
 * A record of each product type
 */
@Data
public class Product {
  @Setter(AccessLevel.PRIVATE)
  private boolean updated = false;

  private Path path;

  private int skuCode;
  private int itemCount;
  private int threshold;
  private int reorderAmount;
  private String title;
  private String description;
  private BigDecimal cost;

  /**
   * Check to see if we have enough of this item for an order
   *
   * @param amount Number of items being ordered
   * @return true if enough stock
   */
  public boolean canOrder(int amount) {
    return (itemCount - amount >= 0);
  }

  /**
   * Place an order, decrement the available itemCount
   *
   * @param amount being ordered
   * @return if order was successfully processed
   */
  public boolean order(int amount) {
    if (canOrder(amount)) {
      itemCount = itemCount - amount;
      setUpdated(true);  // Need to store the updated product information

      // TODO:  add stock management functionality
      return true;
    }

    return false;
  }
}
