/*
 * Copyright (c) 2013-2015 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.europeana.gui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.eledge.android.europeana.R;
import net.eledge.android.europeana.db.model.BlogArticle;
import net.eledge.android.toolkit.gui.GuiUtils;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private final DateFormat formatter = SimpleDateFormat.getDateTimeInstance();

    public final List<BlogArticle> articles = new ArrayList<>();
    private final Context context;

    public BlogAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_home_blog, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        inject(viewHolder, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BlogArticle article = articles.get(position);
        holder.title.setText(article.title);
        holder.content.setText(article.description);
        holder.author.setText(GuiUtils.format(context, R.string.fragment_home_blog_posted, article.author));
        holder.date.setText(formatter.format(article.pubDate));

    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @ViewResource(R.id.listitem_home_blog_textview_title)
        TextView title = null;

        @ViewResource(R.id.listitem_home_blog_textview_text)
        TextView content = null;

        @ViewResource(R.id.listitem_home_blog_textview_author)
        TextView author = null;

        @ViewResource(R.id.listitem_home_blog_textview_date)
        TextView date = null;

        private ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
