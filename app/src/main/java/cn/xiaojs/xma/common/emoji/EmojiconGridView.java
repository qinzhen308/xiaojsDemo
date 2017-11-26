/*
 * Copyright (c) 2016 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.xiaojs.xma.common.emoji;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.xiaojs.xma.R;

/**
 * Created by rockerhieu on 8/10/16.
 */
public class EmojiconGridView extends GridView {

    private EmojiconAdapter mEmojiAdapter;
    private List<Emojicon> mEmojiList;

    public EmojiconGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEmojiList = new ArrayList<>();

        mEmojiList =  Arrays.asList(EmojiMapping.DATA);
        mEmojiAdapter = new EmojiconAdapter(context, mEmojiList);
        setAdapter(mEmojiAdapter);
    }


    public Emojicon getEmoji(int position) {
        return mEmojiAdapter.getItem(position);
    }


//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (onEmojiconClickedListener != null) {
//            onEmojiconClickedListener.onEmojiconClicked((Emojicon) parent.getItemAtPosition(position));
//        }
//    }
}
