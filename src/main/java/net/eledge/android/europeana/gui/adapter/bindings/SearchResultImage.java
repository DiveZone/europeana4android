package net.eledge.android.europeana.gui.adapter.bindings;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.eledge.android.europeana.R;
import net.eledge.android.europeana.search.SearchController;
import net.eledge.android.europeana.search.model.searchresults.Item;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public final class SearchResultImage {

    private SearchResultImage() {
        // NO-OP
    }

    @BindingAdapter("searchResultImageUrl")
    public static void setImageUrl(final ImageView imageView, final String url) {
        final Context context = imageView.getContext();
        if (StringUtils.isNotBlank(url)) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (imageView.getDrawable() != null) {
                                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette palette) {
                                        Item item = SearchController._instance.getItemByImageUrl(url);
                                        if (item != null) {
                                            item.backgroundColor.set(
                                                    palette.getLightMutedColor(
                                                            context.getResources().getColor(R.color.emphasis_transparant)
                                                    )
                                            );
                                        }
                                    }
                                });
                            }
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

}
