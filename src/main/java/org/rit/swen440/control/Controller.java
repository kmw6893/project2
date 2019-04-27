package org.rit.swen440.control;

import org.rit.swen440.dataLayer.Category;
import org.rit.swen440.dataLayer.Product;
import org.rit.swen440.presentation.menu;
import org.rit.swen440.presentation.menumgr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * Controls access to data, on start-up scans directories and builds internal
 * representation of categories and items within each category.  Isolates the
 * categories and products from information on the underlying file system.
 */
public class Controller {
  private String url;
  private String db;
  private String host;
  private String portNum;
  private String username;
  private String password;
  private Set<Category> categories = new HashSet<>();
  private int iSel;

  public  enum PRODUCT_FIELD {
    NAME,
    DESCRIPTION,
    COST,
    INVENTORY
  }

  public Controller(String db, String host, String portNum, String username, String password) {
    this.db = db;
    this.host = host;
    this.portNum = portNum;
    this.username = username;
    this.password = password;
    this.url = "jdbc:mysql://" + host + ":" + portNum + "/" + db;
    loadCategories();
  }

  /**
   * Load the Category information
   *
   *
   */
  private void loadCategories() {
    Statement stmt;
    ResultSet rs;
    String myTable = "SELECT * FROM category";

    try (Connection con = DriverManager.getConnection(url, username, password)) {

      stmt = con.createStatement();
      rs = stmt.executeQuery(myTable);
      while(rs.next()){
        Optional<Category> entry = getCategory(rs.getInt("id"));
        entry.ifPresent(categories::add);
      }

    } catch (SQLException e) {
      throw new IllegalStateException("Cannot connect the database!", e);
    }
  }

  /**
   * Get a list of all category names
   *
   * @return list of categories
   */
  public List<String> getCategories() {

    return categories.stream()
        .map(Category::getName)
        .collect(Collectors.toList());
  }

  /**
   * Get a category name from a product
   *
   * @return a category name
   */
  public String getCategory(String productName) {
    for (Category category : categories) {
      for (Product product : category.getProducts()) {
        if (product.getTitle().equals(productName)) return category.getName();
      }
    }
    return null;
  }


  /**
   * Get the description of the named category
   * @param category name
   * @return description
   */
  public String getCategoryDescription(String category) {
    Optional<Category> match = categories.stream().filter(c -> c.getName().equalsIgnoreCase(category)).findFirst();
    return match.map(Category::getDescription).orElse(null);
  }

  /**
   * Return a list of Products based on the provided category.
   *
   * @param categoryName Name of Category to use
   * @return List of Products in the category
   */
  public List<String> getProducts(String categoryName) {
    Optional<Category> category = findCategory(categoryName);

   return category.map(c -> c.getProducts().stream()
              .map(Product::getTitle)
              .collect(Collectors.toList()))
           .orElse(null);
  }


  public String getProductInformation(String category, String product, PRODUCT_FIELD field) {
   Optional<Product> selectedProduct = getProduct(category, product);
   switch (field) {
     case NAME:
       return selectedProduct.map(Product::getTitle).orElse(null);

     case DESCRIPTION:
       return selectedProduct.map(Product::getDescription).orElse(null);

     case COST:
       return selectedProduct.map(p -> String.format("%.2f", p.getCost())).orElse(null);

     case INVENTORY:
       return selectedProduct.map(p -> String.valueOf(p.getItemCount())).orElse(null);
   }

   return null;
  }

  /**
   * Get the category that matches the provided category name
   *
   * @param name name of catagory
   * @return Category, if present
   */
  public Optional<Category> findCategory(String name) {
    return categories.stream()
        .filter(c -> c.getName().equalsIgnoreCase(name))
        .findFirst();
  }

  /**
   * Get all products that match a passed in search term
   * @param query the search term
   * @return A list of product names
   */
  public List<String> findItem(String query) {
    String search = "SELECT * FROM PRODUCT WHERE TITLE LIKE '%" + query +"%'";
    Statement stmt;
    ArrayList<String> products = new ArrayList<>();
    try (Connection con = DriverManager.getConnection(url, username, password)) {
      stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(search);
      while(rs.next()){
        Product product = loadProduct(
                        rs.getInt("sku"),
                        rs.getInt("item_count"),
                        rs.getInt("threshold"),
                        rs.getInt("recorder_amount"),
                        rs.getString("title"),
                        rs.getString("description"),
                        BigDecimal.valueOf(rs.getFloat("cost"))
        );
        products.add(product.getTitle());
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return products;
  }
  /**
   * Loop through all our categories and write any product records that
   * have been updated.
   */
  public void writeCategories() {
    for (Category category: categories) {
      writeProducts(category.getProducts());
    }
  }

  /* -----------------------------------
   *
   * Private Methods
   */

  /**
   * Get the category object for this directory
   *
   * @param categoryID id of category
   * @return Category object, if .cat file exists
   */
  private Optional<Category> getCategory(int categoryID) {

    Statement stmt1;
    ResultSet rs1;

    Statement stmt2;
    ResultSet rs2;

    String categoryTable = "SELECT * FROM CATEGORY WHERE id = " + categoryID;
    String productTables = "SELECT * FROM PRODUCT WHERE category_id = " + categoryID;

    Category category = new Category();

    try (Connection con = DriverManager.getConnection(url, username, password)) {

      Set<Product> products = new HashSet<>();

      stmt1 = con.createStatement();
      rs1 = stmt1.executeQuery(categoryTable);

      stmt2= con.createStatement();
      rs2 = stmt2.executeQuery(productTables);

      while(rs1.next()){
        category.setName(rs1.getString("name"));
        category.setDescription(rs1.getString("description"));
      }

      while(rs2.next()){
        products.add(
                loadProduct(
                        rs2.getInt("sku"),
                        rs2.getInt("item_count"),
                        rs2.getInt("threshold"),
                        rs2.getInt("recorder_amount"),
                        rs2.getString("title"),
                        rs2.getString("description"),
                        BigDecimal.valueOf(rs2.getFloat("cost"))
                )
        );
      }

      category.setProducts(products);

    } catch (SQLException e) {
      throw new IllegalStateException("Cannot connect the database!", e);
    }

    return Optional.of(category);
  }

  private Optional<Product> getProduct(String category, String product) {
    return findCategory(category).map(c -> c.findProduct(product)).orElse(null);
  }

  /**
   * Parse a subdirectory and create a product object for each product within it
   *
   * @param sku, item_count, threshold, recorder_amount, title, description, cost
   * @return a set of products
   */
  private Product loadProduct(
          int sku,
          int item_count,
          int threshold,
          int recorder_amount,
          String title,
          String description,
          BigDecimal cost
  ) {

    Product product = new Product();

    product.setSkuCode(sku);
    product.setItemCount(item_count);
    product.setThreshold(threshold);
    product.setReorderAmount(recorder_amount);
    product.setTitle(title);
    product.setDescription(description);
    product.setCost(cost);

    return product;
  }

  /**
   * Loop through the set of products and write out any updated products
   *
   * @param products set of products
   */
  private void writeProducts(Set<Product> products) {
    for (Product product : products) {
      if (product.isUpdated()) {
        updateProduct(product);
      }
    }
  }

  /**
   * Write an updated product
   *
   * @param product the product
   */
  private void updateProduct(Product product) {
    try (BufferedWriter writer = Files.newBufferedWriter(product.getPath(), Charset.forName("US-ASCII"))){
      writer.write(String.valueOf(product.getSkuCode()));
      writer.newLine();
      writer.write(String.valueOf(product.getItemCount()));
      writer.newLine();
      writer.write(String.valueOf(product.getThreshold()));
      writer.newLine();
      writer.write(String.valueOf(product.getReorderAmount()));
      writer.newLine();
      writer.write(product.getTitle());
      writer.newLine();
      writer.write(product.getDescription());
      writer.newLine();
      writer.write(product.getCost().toString());
    } catch(IOException e) {
      System.err.println("Failed to write product file for:" + product.getTitle());
    }
  }
}
