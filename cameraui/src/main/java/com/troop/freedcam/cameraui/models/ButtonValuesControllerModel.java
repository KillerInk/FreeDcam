package com.troop.freedcam.cameraui.models;

import com.troop.freedcam.cameraui.models.event.ButtonModelClickEvent;

public class ButtonValuesControllerModel implements ButtonModelClickEvent {

    private ValuesHolderModel valuesHolderModel;

    public ButtonValuesControllerModel(ValuesHolderModel valuesHolderModel)
    {
        this.valuesHolderModel = valuesHolderModel;
    }

    @Override
    public void onButtonClick(ButtonModel model, boolean fromLeft) {
        valuesHolderModel.setButtonModel(model,fromLeft);
    }
}
