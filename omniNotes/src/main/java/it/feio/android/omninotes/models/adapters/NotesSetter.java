package it.feio.android.omninotes.models.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spanned;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.concurrent.RejectedExecutionException;

import it.feio.android.omninotes.async.TextWorkerTask;
import it.feio.android.omninotes.helpers.LogDelegate;
import it.feio.android.omninotes.models.Attachment;
import it.feio.android.omninotes.models.Note;
import it.feio.android.omninotes.models.holders.NoteViewHolder;
import it.feio.android.omninotes.utils.BitmapHelper;
import it.feio.android.omninotes.utils.TextHelper;

public class NotesSetter {

    public static void initDates(Note note, NoteViewHolder holder, Activity mActivity, int navigation) {
        String dateText = TextHelper.getDateText(mActivity, note, navigation);
        holder.date.setText(dateText);
    }

    public static void initIcons(Note note, NoteViewHolder holder, boolean expandedView) {
        // Evaluates the archived state...
        holder.archiveIcon.setVisibility(note.isArchived() ? View.VISIBLE : View.GONE);
        // ...the location
        holder.locationIcon
                .setVisibility(note.getLongitude() != null && note.getLongitude() != 0 ? View.VISIBLE :
                        View.GONE);

        // ...the presence of an alarm
        holder.alarmIcon.setVisibility(note.getAlarm() != null ? View.VISIBLE : View.GONE);
        // ...the locked with password state
        holder.lockedIcon.setVisibility(note.isLocked() ? View.VISIBLE : View.GONE);
        // ...the attachment icon for contracted view
        if (!expandedView) {
            holder.attachmentIcon
                    .setVisibility(!note.getAttachmentsList().isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    public static void initText(Note note, NoteViewHolder holder, Activity mActivity, boolean expandedView) {
        try {
            if (note.isChecklist()) {
                TextWorkerTask task = new TextWorkerTask(mActivity, holder.title, holder.content,
                        expandedView);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, note);
            } else {
                Spanned[] titleAndContent = TextHelper.parseTitleAndContent(mActivity, note);
                holder.title.setText(titleAndContent[0]);
                holder.content.setText(titleAndContent[1]);
                holder.title.setText(titleAndContent[0]);
                if (titleAndContent[1].length() > 0) {
                    holder.content.setText(titleAndContent[1]);
                    holder.content.setVisibility(View.VISIBLE);
                } else {
                    holder.content.setVisibility(View.INVISIBLE);
                }
            }
        } catch (RejectedExecutionException e) {
            LogDelegate.w("Oversized tasks pool to load texts!", e);
        }
    }

    public static void initThumbnail(Note note, NoteViewHolder holder, boolean expandedView, Activity mActivity) {

        if (expandedView && holder.attachmentThumbnail != null) {
            // If note is locked or without attachments nothing is shown
            if ((note.isLocked() && !Prefs.getBoolean("settings_password_access", false))
                    || note.getAttachmentsList().isEmpty()) {
                holder.attachmentThumbnail.setVisibility(View.GONE);
            } else {
                holder.attachmentThumbnail.setVisibility(View.VISIBLE);
                Attachment mAttachment = note.getAttachmentsList().get(0);
                Uri thumbnailUri = BitmapHelper.getThumbnailUri(mActivity, mAttachment);

                Glide.with(mActivity)
                        .load(thumbnailUri)
                        .apply(new RequestOptions().centerCrop())
                        .transition(withCrossFade())
                        .into(holder.attachmentThumbnail);
            }
        }
    }
}
