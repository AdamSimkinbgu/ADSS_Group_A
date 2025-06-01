package Inventory.DAO;

import Inventory.DTO.CategoryDTO;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import Inventory.util.*;

public class CategoryDAO_SQL implements CategoryDAO{


    private Connection conn;

    @Override
    public void addCategory(String name) {

    }

    @Override
    public List<CategoryDTO> getAll() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        try (Statement st = DataBase.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<CategoryDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new CategoryDTO(rs.getInt("id"), rs.getString("name")));
            }
            return list;
        }
    }

    @Override
    public CategoryDTO get(String name) {
        return null;
    }

    @Override
    public void addToCategory(String cat, int pid) {

    }

    @Override
    public void addToCategory(String cat, String sub) {

    }

    @Override
    public void delete(String name) {

    }
}
