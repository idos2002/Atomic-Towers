package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Element {
    public static final int HYDROGEN = 1;
    public static final int HELIUM = 2;
    public static final int LITHIUM = 3;
    public static final int BERYLLIUM = 4;
    public static final int BORON = 5;
    public static final int CARBON = 6;
    public static final int NITROGEN = 7;
    public static final int OXYGEN = 8;

    @SerializedName("name")
    public String name;

    @SerializedName("symbol")
    public String symbol;

    @SerializedName("protons")
    public int protons;

    @SerializedName("neutrons")
    public int neutrons;

    @SerializedName("color")
    public String colorString;

    @SerializedName("summary")
    public String summary;

    public Element(@NonNull Element element) {
        name = element.name;
        symbol = element.symbol;
        protons = element.protons;
        neutrons = element.neutrons;
        colorString = element.colorString;
        summary = element.summary;
    }
}
