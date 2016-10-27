package com.benyuan.xiaojs.refreshview;


/**
 * Created by maxiaobao on 2016/10/27.
 */

public class TestRefresh {

//    package com.benyuan.xiaojs.ui;
//
//    import android.os.Handler;
//    import android.os.Message;
//    import android.support.v7.app.AppCompatActivity;
//    import android.os.Bundle;
//    import android.widget.AbsListView;
//    import android.widget.ArrayAdapter;
//    import android.widget.Toast;
//
//    import com.benyuan.xiaojs.R;
//    import com.myhandmark.pulltorefresh.library.AutoPullToRefreshListView;
//    import com.myhandmark.pulltorefresh.library.AutoRefreshListner;
//
//    import java.util.ArrayList;
//
//
//    public class TestActivity extends AppCompatActivity {
//
//
//        private AutoPullToRefreshListView listView;
//
//        private ArrayList<String> items;
//        private ArrayAdapter<String> adapter;
//
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_test);
//
//            listView = (AutoPullToRefreshListView)findViewById(R.id.alist);
//
//
//            String[] adapterData = new String[] { "Afghanistan", "Albania", "Algeria",
//                    "American Samoa", "Andorra", "Angola", "Anguilla",
//                    "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia",
//                    "Aruba", "Australia", "Austria", "Azerbaijan", "Bahrain",
//                    "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize",
//                    "Benin", "Bermuda", "Bhutan", "Bolivia",
//                    "Bosnia and Herzegovina", "Botswana", "Bouvet Island" };
//
//
//            items = new ArrayList<String>();
//
//            for(int i=0;i<20;i++){
//                items.add("item"+i);
//            }
//
//            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
//
//            listView.setAdapter(adapter);
//
//            listView.setAutoRefresh(true);
//            listView.setAutoRefreshListner(new AutoRefreshListner() {
//                @Override
//                public void onRefresh(AbsListView view) {
//                    //Toast.makeText(TestActivity.this,"hhhhhhhhhh",Toast.LENGTH_SHORT).show();
//
//
//                    listView.showFoooter();
//
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            try {
//                                Thread.sleep(3000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                            for(int i=0;i<5;i++){
//                                items.add("item no"+i);
//                            }
//
//
//                            mh.sendEmptyMessage(0);
//
//                        }
//                    }).start();
//
//
//                }
//            });
//
//
//        }
//
//        private Handler mh = new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//
//                listView.hiddenFoooter();
//
//                adapter.notifyDataSetChanged();
//
//
//
//
//                return false;
//            }
//        });
//    }
}
