/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts.tools;

public class Circle {
    private double centerX;
    private double centerY;
    private double radius;


    public Circle(final double centerX, final double centerY, final double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius  = radius;
    }


    public double getCenterX() { return centerX; }
    public void setCenterX(final double centerX) { this.centerX = centerX; }

    public double getCenterY() { return centerY; }
    public void setCenterY(final double centerY) { this.centerY = centerY; }

    public double getRadius() { return radius; }
    public void setRadius(final double radius) { this.radius = radius; }

    public boolean contains(final double x, final double y) {
        double deltaX = x - centerX;
        double deltaY = y - centerY;
        return (deltaX * deltaX) + (deltaY * deltaY) < radius * radius;
        //return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= radius;
    }
}
