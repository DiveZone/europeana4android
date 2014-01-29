/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
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

package net.eledge.android.eu.europeana.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.toolkit.gui.GuiUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class BlogAdapter extends ArrayAdapter<BlogArticle> {

    private final LayoutInflater inflater;

    private final DateFormat formatter = SimpleDateFormat.getDateTimeInstance();

    public BlogAdapter(Context context, List<BlogArticle> articles) {
        super(context, 0, articles);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArticleViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_home_blog, parent, false);
            holder = new ArticleViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_title);
            holder.content = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_text);
            holder.author = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_author);
            holder.date = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_date);

            convertView.setTag(holder);
        } else {
            holder = (ArticleViewHolder) convertView.getTag();
        }

        BlogArticle article = getItem(position);
        holder.title.setText(article.title);
        holder.content.setText(article.description);
        holder.author.setText(GuiUtils.format(getContext(), R.string.fragment_home_blog_posted, article.author));
        holder.date.setText(formatter.format(article.pubDate));

        return convertView;
    }

    private class ArticleViewHolder {
        TextView title = null;
        TextView content = null;
        TextView author = null;
        TextView date = null;
    }

}
