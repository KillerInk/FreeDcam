/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.ui.themesample.SettingsChildAbstract;

/**
 * Created by troop on 16.06.2015.
 */
public class SimpleValueChild extends FrameLayout implements OnClickListener
{

    private TextView textView;
    private SettingsChildAbstract.CloseChildClick closeNotice;
    public SimpleValueChild(Context context)
    {
        super(context);
        init(context);
    }

    public SimpleValueChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout.cameraui_simplevaluechild, this);
        textView = findViewById(id.simplevaluetext);
        setOnClickListener(this);
    }

    public void SetString(String text, SettingsChildAbstract.CloseChildClick closeNotice)
    {
        textView.setText(text);
        this.closeNotice = closeNotice;
    }

    @Override
    public void onClick(View v)
    {
        closeNotice.onCloseClicked(textView.getText().toString());
    }
}
