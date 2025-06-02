package Inventory.DAO;

import Inventory.DTO.CategoryDTO;

import java.util.*;

import java.sql.*;
import java.util.stream.Collectors;

import Inventory.util.*;

public class CategoryDAO_SQL implements CategoryDAO{


    public CategoryDAO_SQL() {
        // Initialize the connection if needed, or leave it to be managed by the methods.
    }

    @Override
    public CategoryDTO addCategory(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
            return new CategoryDTO(name);
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public List<CategoryDTO> getAll() {
        String categorySql = "SELECT name FROM categories";
        String categoryProductsSql = "SELECT category_name, product_id FROM category_products";
        String subcategorySql = "SELECT parent_name, child_name FROM subcategories";

        Map<String, CategoryDTO> nameToCategory = new HashMap<>();
        Map<String, List<String>> parentToChildren = new HashMap<>();

        try (Connection conn = DataBase.getConnection()) {
            // Step 1: Load all categories
            try (PreparedStatement ps = conn.prepareStatement(categorySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    nameToCategory.put(name, new CategoryDTO(name));
                }
            }catch (Exception e){
                throw new RuntimeException("SQL Exception: " + e.getMessage());
            }

            // Step 2: Load category products
            try (PreparedStatement ps = conn.prepareStatement(categoryProductsSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String categoryName = rs.getString("category_name");
                    int productId = rs.getInt("product_id");

                    CategoryDTO category = nameToCategory.get(categoryName);
                    if (category != null) {
                        category.AddProduct(productId);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("SQL Exception: " + e.getMessage());

            }


            // Step 3: Load subcategory relationships (name-based)
            try (PreparedStatement ps = conn.prepareStatement(subcategorySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String parent = rs.getString("parent_name");
                    String child = rs.getString("child_name");

                    if(parentToChildren.keySet().contains(parent)){
                        parentToChildren.get(parent).add(child);
                    }else{
                        parentToChildren.put(parent, new ArrayList<>());
                        parentToChildren.get(parent).add(child);
                    }
                }
            }
            catch (Exception e){
                throw new RuntimeException("SQL Exception: " + e.getMessage());
            }

            // Step 4: Link subcategories
            for (var entry : parentToChildren.entrySet()) {
                String parentName = entry.getKey();
                CategoryDTO parent = nameToCategory.get(parentName);
                if (parent == null) continue;

                for (String childName : entry.getValue()) {
                    CategoryDTO child = nameToCategory.get(childName);
                    if (child != null) {
                        parent.AddSubcategory(child);
                    }
                }
            }

            // Step 5: Return only top-level categories
            Set<String> allChildren = parentToChildren.values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());

            return nameToCategory.values()
                    .stream()
                    .filter(cat -> !allChildren.contains(cat.getName()))
                    .collect(Collectors.toList());


        }
        catch (Exception e){
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public CategoryDTO get(String name) {
        String categorySql = "SELECT name FROM categories WHERE name = ?";
        String subcategorySql = "SELECT child_name FROM subcategories WHERE parent_name = ?";

        try (Connection conn = DataBase.getConnection()) {
            // Step 1: Load the main category
            CategoryDTO category = null;
            try (PreparedStatement ps = conn.prepareStatement(categorySql)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        category = new CategoryDTO(name);
                    } else {
                        return null; // not found
                    }
                }
            }

            // Step 2: Load subcategories
            try (PreparedStatement ps = conn.prepareStatement(subcategorySql)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String subName = rs.getString("child_id");
                        CategoryDTO subCategory = get(subName); // recursively fetch subcategory
                        if (subCategory != null) {
                            category.AddSubcategory(subCategory);
                        }
                    }
                }
            }

            return category;

        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void addToCategory(String cat, int pid) {
        String sql = "INSERT INTO category_products (category_name, product_id) VALUES (?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat);
            ps.setInt(2, pid);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void addToCategory(String cat, String sub) {
        String sql = "INSERT INTO subcategories (parent_name, child_name) VALUES (?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat);
            ps.setString(2, sub);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String deleteCategoriesSql = "DELETE FROM categories";
        String deleteSubcategoriesSql = "DELETE FROM subcategories";
        String deleteCategoryProductsSql = "DELETE FROM category_products";

        try (Connection conn = DataBase.getConnection()) {
            // Delete all subcategories first
            try (PreparedStatement ps = conn.prepareStatement(deleteSubcategoriesSql)) {
                ps.executeUpdate();
            }

            // Delete all products in categories
            try (PreparedStatement ps = conn.prepareStatement(deleteCategoryProductsSql)) {
                ps.executeUpdate();
            }

            // Finally, delete all categories
            try (PreparedStatement ps = conn.prepareStatement(deleteCategoriesSql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void delete(String name) {
        String deleteCategorySql = "DELETE FROM categories WHERE name = ?";
        String deleteSubcategoriesSql = "DELETE FROM subcategories WHERE parent_name = ?";
        String deleteCategoryProductsSql = "DELETE FROM category_products WHERE category_name = ?";

        try (Connection conn = DataBase.getConnection()) {
            // Delete subcategories first
            try (PreparedStatement ps = conn.prepareStatement(deleteSubcategoriesSql)) {
                ps.setString(1, name);
                ps.executeUpdate();
            }

            // Delete products in the category
            try (PreparedStatement ps = conn.prepareStatement(deleteCategoryProductsSql)) {
                ps.setString(1, name);
                ps.executeUpdate();
            }

            // Finally, delete the category itself
            try (PreparedStatement ps = conn.prepareStatement(deleteCategorySql)) {
                ps.setString(1, name);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }
}
