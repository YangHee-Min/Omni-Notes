package it.feio.android.omninotes.models.adapters;


import static it.feio.android.omninotes.utils.ConstantsBase.PREF_COLORS_APP_DEFAULT;

import android.graphics.Color;
import android.view.View;

import com.pixplicity.easyprefs.library.Prefs;

import it.feio.android.omninotes.R;
import it.feio.android.omninotes.models.Note;
import it.feio.android.omninotes.models.holders.NoteViewHolder;

public class NoteColorer {
    @SuppressWarnings("unused")
    public static void colorNote(Note note, View v) {
        colorNote(note, v, null);
    }
    /**
     * Color of category marker if note is categorized a function is active in preferences
     */
    public static void colorNote(Note note, View v, NoteViewHolder holder) {

        String colorsPref = Prefs.getString("settings_colors_app", PREF_COLORS_APP_DEFAULT);

        // Checking preference
        if (!colorsPref.equals("disabled")) {

            // Resetting transparent color to the view
            v.setBackgroundColor(Color.parseColor("#00000000"));

            // If category is set the color will be applied on the appropriate target
            if (note.getCategory() != null && note.getCategory().getColor() != null) {
                if (colorsPref.equals("complete") || colorsPref.equals("list")) {
                    v.setBackgroundColor(Integer.parseInt(note.getCategory().getColor()));
                } else {
                    if (holder != null) {
                        holder.categoryMarker
                                .setBackgroundColor(Integer.parseInt(note.getCategory().getColor()));
                    } else {
                        v.findViewById(R.id.category_marker)
                                .setBackgroundColor(Integer.parseInt(note.getCategory().getColor()));
                    }
                }
            } else {
                v.findViewById(R.id.category_marker).setBackgroundColor(0);
            }
        }
    }
}



