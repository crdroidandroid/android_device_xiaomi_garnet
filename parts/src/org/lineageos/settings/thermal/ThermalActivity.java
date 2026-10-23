/*
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.thermal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarAppCompatActivity;
import com.android.settingslib.widget.ExpressiveDesignEnabledProvider;

import org.lineageos.settings.R;

public class ThermalActivity extends CollapsingToolbarAppCompatActivity
        implements ExpressiveDesignEnabledProvider {
    @Override
    public boolean isExpressiveDesignEnabled() {
        return true;
    }

    private static final String TAG_THERMAL = "thermal";
    private static final String THERMAL_SCONFIG = "/sys/class/thermal/thermal_message/sconfig";

    // The search is the toolbar's own expandable SearchView action item,
    // the way the system's app lists do it: tapping the search button
    // turns the toolbar itself into the search field, in place. While it
    // is expanded, the collapsing header is forced to its compact height
    // (see setHeaderCompact), so no tall empty header is left between
    // the field and the list.
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private boolean mSearchMode;
    private int mExpandedHeaderHeight;

    private final OnBackPressedCallback mSearchBackCallback =
            new OnBackPressedCallback(false) {
                @Override
                public void handleOnBackPressed() {
                    closeSearch();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(com.android.settingslib.collapsingtoolbar.R.id.content_frame,
                new ThermalSettingsFragment(), TAG_THERMAL).commit();

        getOnBackPressedDispatcher().addCallback(this, mSearchBackCallback);

        // The toolbar's built-in action button is a wide rectangle and
        // is not used; the search button is the options menu item below.
        setActionButtonEnabled(false);

        // The button the toolbar shows while the search field is
        // expanded: use the same expressive back (circle + arrow) as the
        // toolbar's own back button.
        final androidx.appcompat.widget.Toolbar toolbar =
                findToolbar(getWindow().getDecorView());
        if (toolbar != null) {
            toolbar.setCollapseIcon(com.android.settingslib.collapsingtoolbar.R.drawable
                    .settingslib_expressive_icon_back);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thermal_search_menu, menu);
        mSearchItem = menu.findItem(R.id.action_thermal_search);
        if (mSearchItem == null) {
            return true;
        }
        final Resources res = getResources();
        final String pkg = getPackageName();
        final int onSurface = res.getIdentifier(
                "settingslib_materialColorOnSurface", "color", pkg);
        final int onSurfaceVariant = res.getIdentifier(
                "settingslib_materialColorOnSurfaceVariant", "color", pkg);
        final int primary = res.getIdentifier(
                "settingslib_materialColorPrimary", "color", pkg);
        if (onSurfaceVariant != 0) {
            mSearchItem.setIconTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, onSurfaceVariant)));
        }

        mSearchView = (SearchView) mSearchItem.getActionView();
        if (mSearchView != null) {
            mSearchView.setQueryHint(getString(R.string.thermal_search_hint_short));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    hideKeyboard();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    final ThermalSettingsFragment fragment = getThermalFragment();
                    if (fragment != null) {
                        fragment.setSearchQuery(newText);
                    }
                    return true;
                }
            });
            styleSearchView(onSurface, onSurfaceVariant, primary);
        }

        alignOpenButtonWithCloseButton();

        mSearchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchMode = true;
                mSearchBackCallback.setEnabled(true);
                setHeaderCompact(true);
                // Expanding makes the SearchView refresh its internals
                // (the magnifier inside the field comes back, and the
                // hint gets decorated with a magnifier glyph); strip
                // both again once it settles.
                if (mSearchView != null) {
                    mSearchView.post(() -> polishExpandedSearch());
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchMode = false;
                mSearchBackCallback.setEnabled(false);
                setHeaderCompact(false);
                final ThermalSettingsFragment fragment = getThermalFragment();
                if (fragment != null) {
                    fragment.setSearchQuery("");
                }
                return true;
            }
        });
        return true;
    }

    // Put the collapsed search icon exactly where the expanded field's
    // close (X) button sits, so opening the search does not make the
    // icon jump sideways. In the SearchView's own layout the X's center
    // is 28dp in from the end (8dp edit-frame margin + half of the 40dp
    // button), while a toolbar action button centers its icon 24dp in
    // (half of the 48dp minimum width): shift the icon the 4dp over.
    private void alignOpenButtonWithCloseButton() {
        final androidx.appcompat.widget.Toolbar toolbar =
                findToolbar(getWindow().getDecorView());
        if (toolbar == null) {
            return;
        }
        toolbar.post(() -> {
            final androidx.appcompat.widget.ActionMenuView menuView =
                    findActionMenuView(toolbar);
            if (menuView == null) {
                return;
            }
            for (int i = 0; i < menuView.getChildCount(); i++) {
                final View child = menuView.getChildAt(i);
                if (child instanceof androidx.appcompat.view.menu.ActionMenuItemView) {
                    final androidx.appcompat.view.menu.ActionMenuItemView itemView =
                            (androidx.appcompat.view.menu.ActionMenuItemView) child;
                    if (itemView.getItemData() != null
                            && itemView.getItemData().getItemId()
                                    == R.id.action_thermal_search) {
                        itemView.setTranslationX(-dp(4));
                    }
                }
            }
        });
    }

    private static androidx.appcompat.widget.ActionMenuView findActionMenuView(View view) {
        if (view instanceof androidx.appcompat.widget.ActionMenuView) {
            return (androidx.appcompat.widget.ActionMenuView) view;
        }
        if (view instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                final androidx.appcompat.widget.ActionMenuView found =
                        findActionMenuView(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Make the expanded search field match the rest of the screen: no
    // magnifier inside the field, no underline, the theme's text colors,
    // a Monet cursor, and the field pulled toward the back button (the
    // stock SearchView keeps extra start insets for the magnifier that
    // is not shown here).
    private void styleSearchView(int onSurface, int onSurfaceVariant, int primary) {
        final EditText srcText = mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (srcText != null) {
            srcText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            srcText.setPadding(0, srcText.getPaddingTop(), srcText.getPaddingRight(),
                    srcText.getPaddingBottom());
            if (onSurface != 0) {
                srcText.setTextColor(ContextCompat.getColor(this, onSurface));
            }
            if (onSurfaceVariant != 0) {
                srcText.setHintTextColor(ContextCompat.getColor(this, onSurfaceVariant));
            }
            if (primary != 0) {
                final android.graphics.drawable.GradientDrawable cursor =
                        new android.graphics.drawable.GradientDrawable();
                cursor.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                cursor.setSize(dp(2), 0);
                cursor.setColor(ContextCompat.getColor(this, primary));
                srcText.setTextCursorDrawable(cursor);
            }
        }
        final View plate = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (plate != null) {
            plate.setBackground(null);
        }
        final View editFrame = mSearchView.findViewById(androidx.appcompat.R.id.search_edit_frame);
        if (editFrame != null) {
            editFrame.setPadding(0, editFrame.getPaddingTop(), editFrame.getPaddingRight(),
                    editFrame.getPaddingBottom());
            if (editFrame.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams lp =
                        (ViewGroup.MarginLayoutParams) editFrame.getLayoutParams();
                lp.setMarginStart(0);
                editFrame.setLayoutParams(lp);
            }
        }
        final ImageView magIcon = mSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (magIcon != null) {
            magIcon.setVisibility(View.GONE);
        }
        final ImageView closeButton =
                mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeButton != null && onSurfaceVariant != 0) {
            closeButton.setColorFilter(ContextCompat.getColor(this, onSurfaceVariant));
        }
    }

    private void polishExpandedSearch() {
        if (mSearchView == null) {
            return;
        }
        final ImageView magIcon = mSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (magIcon != null) {
            magIcon.setVisibility(View.GONE);
        }
        // Replace the hint: the SearchView decorates its hint with a
        // magnifier glyph, and there is no switch for that; a plain hint
        // set directly on the text field has no glyph.
        final EditText srcText = mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (srcText != null) {
            srcText.setHint(getString(R.string.thermal_search_hint_short));
        }
    }

    // Force the collapsing header to (or back from) its compact height
    // by resizing the CollapsingToolbarLayout itself: at the toolbar's
    // height there is no expanded title area left, the toolbar fills
    // the header exactly, and the list sits right below it. This is a
    // plain layout height, so keyboard resizes or scroll state cannot
    // undo it while searching.
    private void setHeaderCompact(boolean compact) {
        final View collapsing = findViewById(
                com.android.settingslib.collapsingtoolbar.R.id.collapsing_toolbar);
        if (collapsing == null) {
            return;
        }
        final ViewGroup.LayoutParams lp = collapsing.getLayoutParams();
        if (compact) {
            if (mExpandedHeaderHeight <= 0 && lp.height > 0) {
                mExpandedHeaderHeight = lp.height;
            }
            final androidx.appcompat.widget.Toolbar toolbar =
                    findToolbar(getWindow().getDecorView());
            lp.height = toolbar != null && toolbar.getHeight() > 0
                    ? toolbar.getHeight() : dp(56);
        } else {
            lp.height = mExpandedHeaderHeight > 0
                    ? mExpandedHeaderHeight
                    : getResources().getDimensionPixelSize(com.android.settingslib
                            .collapsingtoolbar.R.dimen.settingslib_toolbar_layout_height);
        }
        collapsing.setLayoutParams(lp);
    }

    private void closeSearch() {
        if (mSearchItem != null && mSearchItem.isActionViewExpanded()) {
            mSearchItem.collapseActionView();
        }
    }

    private void hideKeyboard() {
        if (mSearchView == null) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        }
    }

    private ThermalSettingsFragment getThermalFragment() {
        return (ThermalSettingsFragment) getFragmentManager().findFragmentByTag(TAG_THERMAL);
    }

    private static androidx.appcompat.widget.Toolbar findToolbar(View view) {
        if (view instanceof androidx.appcompat.widget.Toolbar) {
            return (androidx.appcompat.widget.Toolbar) view;
        }
        if (view instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                final androidx.appcompat.widget.Toolbar found =
                        findToolbar(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
