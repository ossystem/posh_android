package ru.jufy.myposh.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.activities.MainActivity;

import static ru.jufy.myposh.R.drawable.circle;

/**
 * Created by BorisDev on 16.08.2017.
 */

public class TextEditorFragment extends Fragment {
    private static final int initialFillColor = 0xFF0099FF;
    private static final int initialFontColor = 0xFF000000;
    private View rootView;
    FloatingActionButton fabCancel;
    EditText textEditor;
    ImageView circle;
    ImageView keyBoard;
    ImageView font;
    ImageView fontColor;
    ImageView fillColor;
    private RecyclerView fontsView = null;
    private ColorPickerView fontColorPicker;
    private ColorPickerView fillColorPicker;
    private int currentFillColor = initialFillColor;

    public TextEditorFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_text_editor, container, false);

        fabCancel = (FloatingActionButton)rootView.findViewById(R.id.fab_cancel);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showCurrentFragment();
            }
        });

        textEditor = (EditText)rootView.findViewById(R.id.textEditor);
        textEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        textEditor.setTextColor(initialFontColor);
        textEditor.setClipToOutline(true);

        circle = (ImageView)rootView.findViewById(R.id.circle);
        GradientDrawable bg = (GradientDrawable) circle.getDrawable();
        bg.setColor(initialFillColor);

        keyBoard = (ImageView) rootView.findViewById(R.id.iconKeyboard);
        keyBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateKeyboard();
            }
        });

        font = (ImageView) rootView.findViewById(R.id.iconFont);
        font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateFont();
            }
        });

        fontColor = (ImageView) rootView.findViewById(R.id.iconFontColor);
        fontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateFontColor();
            }
        });

        fillColor = (ImageView) rootView.findViewById(R.id.iconFill);
        fillColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateFillColor();
            }
        });

        ((MainActivity)getActivity()).hideBottomNav();

        return rootView;
    }

    private void activateKeyboard() {
        inactivateAll();
        setIcon(keyBoard, R.drawable.icon_keyboard_red);
        textEditor.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textEditor, InputMethodManager.SHOW_IMPLICIT);
    }

    private void inactivateAll() {
        inactivateKeyboard();
        inactivateFont();
        inactivateFontColor();
        inactivateFillColor();
    }

    private void inactivateKeyboard() {
        setIcon(keyBoard, R.drawable.icon_keyboard_pink);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textEditor.getWindowToken(), 0);
    }

    private void activateFont() {
        inactivateAll();
        setIcon(font, R.drawable.icon_font_red);
        addFontsList();
    }

    private void addFontsList() {
        fontsView = new RecyclerView(getContext());
        fontsView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        fontsView.setLayoutManager(new LinearLayoutManager(getContext()));
        FontsAdapter adapter = new FontsAdapter(getContext());
        fontsView.setAdapter(adapter);
        ((LinearLayout)rootView).addView(fontsView);
    }

    private void inactivateFont() {
        setIcon(font, R.drawable.icon_font_pink);
        if (null != fontsView) {
            ((LinearLayout) rootView).removeView(fontsView);
            fontsView = null;
        }
    }

    private void activateFontColor() {
        inactivateAll();
        setIcon(fontColor, R.drawable.icon_font_color_red);
        fontColorPicker = new ColorPickerView(getContext());
        fontColorPicker.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        fontColorPicker.setAlphaSliderVisible(true);
        fontColorPicker.setColor(textEditor.getCurrentTextColor(), true);
        fontColorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                textEditor.setTextColor(newColor);
            }
        });
        ((LinearLayout)rootView).addView(fontColorPicker);
    }

    private void inactivateFontColor() {
        setIcon(fontColor, R.drawable.icon_font_color_pink);
        if (null != fontColorPicker) {
            ((LinearLayout) rootView).removeView(fontColorPicker);
            fontColorPicker = null;
        }
    }

    private void activateFillColor() {
        inactivateAll();
        setIcon(fillColor, R.drawable.icon_fill_red);
        fillColorPicker = new ColorPickerView(getContext());
        fillColorPicker.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        fillColorPicker.setAlphaSliderVisible(true);
        fillColorPicker.setColor(currentFillColor, true);
        fillColorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                currentFillColor = newColor;
                GradientDrawable bg = (GradientDrawable) circle.getDrawable();
                bg.setColor(newColor);
            }
        });
        ((LinearLayout)rootView).addView(fillColorPicker);
    }

    private void inactivateFillColor() {
        setIcon(fillColor, R.drawable.icon_fill_pink);
        if (null != fillColorPicker) {
            ((LinearLayout) rootView).removeView(fillColorPicker);
            fillColorPicker = null;
        }
    }

    private void setIcon(ImageView view, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(getResources().getDrawable(id, MyPoshApplication.getContext().getTheme()));
        } else {
            view.setImageDrawable(getResources().getDrawable(id));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        activateKeyboard();
    }

    class FontsAdapter extends RecyclerView.Adapter<FontsAdapter.FontHolder> {

        List<Pair<String, Typeface>> items;
        Context context;

        FontsAdapter(Context context) {
            super();
            this.context = context;
            this.items = getSSystemFontMap();
        }

        private List<Pair<String, Typeface>> getSSystemFontMap() {
            Map<String, Typeface> sSystemFontMap = null;
            List<Pair<String, Typeface>> result = new ArrayList<>();
            try {
                Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                Field f = Typeface.class.getDeclaredField("sSystemFontMap");
                f.setAccessible(true);
                sSystemFontMap = (Map<String, Typeface>) f.get(typeface);
                for (Map.Entry<String, Typeface> entry : sSystemFontMap.entrySet()) {
                    Pair<String, Typeface> font = new Pair<>(entry.getKey(), entry.getValue());
                    result.add(font);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public FontsAdapter.FontHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new FontsAdapter.FontHolder(v);
        }

        @Override
        public void onBindViewHolder(final FontsAdapter.FontHolder holder, int position) {
            holder.item.setText(items.get(position).first);
            holder.item.setTypeface(items.get(position).second);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textEditor.setTypeface(items.get(holder.getAdapterPosition()).second);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class FontHolder extends RecyclerView.ViewHolder {
            TextView item;
            public FontHolder(View v) {
                super(v);
                item = (TextView)itemView.findViewById(R.id.list_item_text);
            }
        }
    }
}
