package com.example.atomictowers.data.game;

import com.google.gson.annotations.SerializedName;

public class AtomType {

    @SerializedName("name")
    public String name;

    @SerializedName("symbol")
    public String symbol;

    @SerializedName("protons")
    public int protons = 0;

    @SerializedName("neutrons")
    public int neutrons = 0;

    @SerializedName("color")
    public String colorString;

    @SerializedName("summary")
    public String summary;
}
