package com.troop.freedcam.eventbus.events;

import android.graphics.Matrix;

public class TransformMatrixChangedEvent {

    public final Matrix matrix;

    public TransformMatrixChangedEvent(Matrix matrix)
    {
        this.matrix = matrix;
    }
}
