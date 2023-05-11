package com.example.wisatawakatobi.recommendation;

import android.util.Log;

import com.example.wisatawakatobi.model.Wisata;

import java.util.ArrayList;
import java.util.List;

public class NBClassifier {
    // Naive Bayes Algorithm
    public static List<Wisata> recommendItems(List<Wisata> items, List<Wisata> favorites) {
        List<Wisata> recommendedItems = new ArrayList<>();

        // Calculate probability of each item based on user's favorites
        double totalFavorites = favorites.size();
        double[] itemProbabilities = new double[items.size()];

        for (int i = 0; i < items.size(); i++) {
            Wisata item = items.get(i);
            double categoryCount = 0;
            double visitorCount = 0;

            for (Wisata favorite : favorites) {
                if (favorite.getKategori().equals(item.getKategori())) {
                    categoryCount++;
                }
                if (Integer.parseInt(favorite.getJumlah()) >= Integer.parseInt(item.getJumlah())) {
                    visitorCount++;
                }
            }

            // Adjust the weights for category and visitor count based on your preference
            double categoryWeight = 0.6; // Weight for category factor
            double visitorWeight = 0.4; // Weight for monthly visitor factor

            double categoryProbability = categoryCount / totalFavorites;
            double visitorProbability = visitorCount / totalFavorites;

            itemProbabilities[i] = (categoryWeight * categoryProbability) + (visitorWeight * visitorProbability);
        }

        // Sort items based on probabilities
        for (int i = 0; i < items.size() - 1; i++) {
            for (int j = 0; j < items.size() - i - 1; j++) {
                if (itemProbabilities[j] < itemProbabilities[j + 1]) {
                    // Swap items
                    double tempProb = itemProbabilities[j];
                    itemProbabilities[j] = itemProbabilities[j + 1];
                    itemProbabilities[j + 1] = tempProb;

                    Wisata tempItem = items.get(j);
                    items.set(j, items.get(j + 1));
                    items.set(j + 1, tempItem);
                }
            }
        }

        // Get top 5 recommended items
        for (int i = 0; i < Math.min(5, items.size()); i++) {
            recommendedItems.add(items.get(i));
        }
        Log.d("NB items", items.toString());
        Log.d("NB fav", favorites.toString());
        Log.d("NB rec", recommendedItems.toString());

        return recommendedItems;
    }
}