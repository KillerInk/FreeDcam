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

package freed.cam.ui.themesample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by troop on 18.03.2016.
 * This class allows to disable ViewPagers touch events.
 * when the metering rectangle gets moved left or right it tends to switch fragments
 * with disabling touch while metering is moved that is avoided
 */
public class PagingView extends ViewPager
{

    private boolean allowScroll;

    public PagingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        allowScroll = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (allowScroll) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (allowScroll) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void EnableScroll(boolean enabled) {
        allowScroll = enabled;
    }
}
