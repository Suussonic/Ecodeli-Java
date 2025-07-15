package com.example.ecodeli2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

public class UserData {

    private static final JsonNode userRoot;
    static {
        JsonNode tmp = null;
        try (InputStream is = UserData.class.getResourceAsStream("user.json")) {
            ObjectMapper mapper = new ObjectMapper();
            tmp = mapper.readTree(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        userRoot = tmp;
    }

    public static Map<String, Integer> getUserRoleDistribution() {
        Map<String, Integer> roleDistribution = new HashMap<>();
        
        if (userRoot == null || !userRoot.isArray()) {
            return roleDistribution;
        }

        // Initialiser les compteurs pour tous les rôles
        roleDistribution.put("Admin", 0);
        roleDistribution.put("Support", 0);
        roleDistribution.put("HR", 0);
        roleDistribution.put("Employee", 0);
        roleDistribution.put("Supplier", 0);
        roleDistribution.put("Service Provider", 0);
        roleDistribution.put("Delivery", 0);
        roleDistribution.put("User", 0);

        // Compter les utilisateurs par rôle
        for (JsonNode user : userRoot) {
            int role = user.get("role").asInt();
            String roleName = getRoleName(role);
            roleDistribution.put(roleName, roleDistribution.get(roleName) + 1);
        }

        // Supprimer les rôles avec 0 utilisateurs pour un affichage plus propre
        roleDistribution.entrySet().removeIf(entry -> entry.getValue() == 0);

        return roleDistribution;
    }

    private static String getRoleName(int role) {
        switch (role) {
            case 0: return "Admin";
            case 1: return "Support";
            case 2: return "HR";
            case 3: return "Employee";
            case 4: return "Supplier";
            case 5: return "Service Provider";
            case 6: return "Delivery";
            case 7: return "User";
            default: return "Unknown";
        }
    }
}
