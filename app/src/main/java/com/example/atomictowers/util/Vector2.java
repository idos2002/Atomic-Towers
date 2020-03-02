package com.example.atomictowers.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Vector2 {

    public static final Vector2 ZERO = new Vector2(0, 0);

    public static final Vector2 UNIT_RIGHT = new Vector2(1, 0);
    public static final Vector2 UNIT_DOWN = new Vector2(0, 1);
    public static final Vector2 UNIT_LEFT = new Vector2(-1, 0);
    public static final Vector2 UNIT_UP = new Vector2(0, -1);

    @SerializedName("x")
    public float x;

    @SerializedName("y")
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new vector from polar form.
     *
     * @param magnitude The magnitude of the vector
     * @param angle     The angle in radians the vector creates with the positive x direction.
     * @return A new {@link Vector2} instance.
     */
    @NonNull
    public static Vector2 fromPolar(float magnitude, float angle) {
        float x = magnitude * (float) Math.cos(angle);
        float y = magnitude * (float) Math.sin(angle);

        return new Vector2(x, y);
    }

    @NonNull
    public static Vector2 unit(float angle) {
        float x = (float) Math.cos(angle);
        float y = (float) Math.sin(angle);

        return new Vector2(x, y);
    }

    public float magnitude() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    @NonNull
    public Vector2 toUnit() {
        return this.scale(1 / this.magnitude());
    }

    public float angle() {
        return (float) Math.atan2(this.y, this.x);
    }

    /**
     * Sets the vector's coordinates using vector polar form.
     *
     * @param length The length of the vector
     * @param angle  The angle in radians the vector creates with the positive x direction.
     */
    public void setPolar(float length, float angle) {
        this.x = length * (float) Math.cos(angle);
        this.y = length * (float) Math.sin(angle);
    }

    @NonNull
    public Vector2 add(@NonNull Vector2 v) {
        return new Vector2(this.x + v.x, this.y + v.y);
    }

    @NonNull
    public Vector2 subtract(@NonNull Vector2 v) {
        return new Vector2(this.x - v.x, this.y - v.y);
    }

    /**
     * Vector-scalar multiplication. Scales the {@link Vector2} by {@code scalar}.
     *
     * @param scalar The scalar to scale the {@link Vector2} by.
     * @return A new scaled vector.
     */
    @NonNull
    public Vector2 scale(float scalar) {
        return new Vector2(scalar * this.x, scalar * this.y);
    }

    @NonNull
    public Vector2 negate() {
        return scale(-1);
    }

    /**
     * Rotates the vector according to a given angle.
     * Formula by <a href="https://matthew-brett.github.io/teaching/rotation_2d.html">Matthew Brett</a>.
     *
     * @param angle The angle in radians to rotate the vector with.
     * @return A new rotated vector.
     */
    @NonNull
    public Vector2 rotate(float angle) {
        float x = (float) (Math.cos(angle * this.x) - Math.sin(angle * this.y));
        float y = (float) (Math.sin(angle * this.x) + Math.cos(angle * this.y));

        return new Vector2(x, y);
    }

    public boolean equals(@NonNull Vector2 other) {
        return this.x == other.x && this.y == other.y;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", this.x, this.y);
    }
}
