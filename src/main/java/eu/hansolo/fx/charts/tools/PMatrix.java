/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
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


/**
 * Projection Matrix
 * Used in CubeChart for Isometric projection
 */
public class PMatrix {
    public P2d xAxis;
    public P2d yAxis;
    public P2d zAxis;
    public P3d depth;
    public P2d origin;


    public PMatrix(final P2d xAxis, final P2d yAxis, final P2d zAxis, final P3d depth, final P2d origin) {
        this.xAxis  = xAxis;
        this.yAxis  = yAxis;
        this.zAxis  = zAxis;
        this.depth  = depth;
        this.origin = origin;
    }


    public void setProjection(final PMatrix pMatrix) {
        this.xAxis = pMatrix.xAxis;
        this.yAxis = pMatrix.yAxis;
        this.zAxis = pMatrix.zAxis;
        if (null == pMatrix.depth) {
            this.depth = null == pMatrix.depth ? new P3d(this.xAxis.y, this.yAxis.y, -this.zAxis.y) : pMatrix.depth;
        }
    }


    public P3d project(final P3d p) {
        double x = p.x * this.xAxis.x + p.y * this.yAxis.x + p.z * this.zAxis.x + this.origin.x;
        double y = p.x * this.xAxis.y + p.y * this.yAxis.y + p.z * this.zAxis.y + this.origin.y;
        double z = p.x * this.depth.x + p.y * this.depth.y + p.z * this.depth.z;
        return new P3d(x, y, z);
    }
}
