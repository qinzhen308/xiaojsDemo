package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CategoriesManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.RegisterDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.VerifyCode;

import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import java.util.Date;

public class TestAPIActivity extends Activity {

    private int vcode;
    private long mob = 13812345687l;
    private String subject = "5820a10e101db0af4bcf2fd9";
    private String sessionid = "oyPm--oJmTko27XHnaxoyyr0aNgGs424";
    private String id = "582bc19580144c1b773611e9";
    private String token = "";///"7ILUcJUmJGSmyeaB3QNWpUlPdT3-E2MXkC1oZhqJ:HBHjxd_oTlAiwaht4niHQFDmh6Q=:eyJyZXR1cm5Cb2R5Ijoie1xuICAgICAgICAgICAgXCJuYW1lXCI6ICQoZm5hbWUpLFxuICAgICAgICAgICAgXCJzaXplXCI6ICQoZnNpemUpLFxuICAgICAgICAgICAgXCJ3XCI6ICQoaW1hZ2VJbmZvLndpZHRoKSxcbiAgICAgICAgICAgIFwiaFwiOiAkKGltYWdlSW5mby5oZWlnaHQpLFxuICAgICAgICAgICAgXCJoYXNoXCI6ICQoZXRhZylcbiAgICAgICAgfSIsInNjb3BlIjoidW5kZWZpbmVkOjU4MjkyMGFmYzcwMDkzM2M0ZDZjMjVkYyIsImRlYWRsaW5lIjoxNDc5MjQ4NDA2fQ==";


    private final static int CROP_PORTRAIT = 100;


    private ImageView imageView;

    private IWXAPI iwxapi;
    private Tencent tencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);

//        ClassLessonView view = new ClassLessonView(this);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        setContentView(view);

        imageView = (ImageView) findViewById(R.id.loading);
//
//
        Glide.with(this).load(R.drawable.login_logo).into(imageView);
        //InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        //ExternalCacheDiskCacheFactory

//
//        iwxapi = WechatUtil.registerToWechat(this);
//        tencent = QQUtil.getTencent(this.getApplicationContext());




    }


    public void btnClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_svc: {
                //testSendCode(this);
//                Intent intent = new Intent(this,CaptureActivity.class);
//                startActivity(intent);


                break;
            }
            case R.id.btn_svm: {
                //testValidateCode(this);
                //testRegister(this);
                //testLogin(this);
                //testGetUpToken(this);
                //testCenterData(this);

//                Intent i = new Intent(this,ContactActivity.class);
//                startActivity(i);

                testCache();

                break;
            }
            case R.id.btn_q: {
                //testValidateCode(this);
                //testRegister(this);
                //testLogin(this);
                //testLogout(this);
                //testClaimCompetency(this);
                //testCreateLession(this);
                //testGetLessons(this);
                //testPutLessonOnShelves(this);
                //testJsonFormat();
                //testGetSubject(this);
                //testGetUpToken(this);
                //editProfile(this);
                //getProfile(this);
                //testCoverUpLoad(this);
                //testLessonHomePage(this);
                //editPortrait();
                //testEditLesson(this);
                //confirmEnllor(this);
//                testHUB(this);
                //testCenterData(this);
                //crash();
               // testGetData(this);
                //testLoading();
                testT(this);

                break;
            }

        }
    }


    public void testCache() {


        //HelloWordStateMachine.makeHelloWorld();

//        String ticket = "2d518d2be219874f8a6bde464faba5b3c54de27101714972e79397ca5e4a84e4d74068124620fa2d72b9fa033b85a9d23ed25a3b47c380cc28e2f576d7ceac50";
//        String secret = "9811f5b7db45971b7cda2d81f0ada513ddc7c8f0507d4c58df29ba75cd73ca842ef4ada36977b5a368393367cac5168dc39dd0991d598a3cae150e1abd5dcabedd33977e33bdf316f9ec8f9c33d8e7005868f8e843bae086af27d6bda4b44e9a2d148510157b1183eb4b8bbfd72bcb7547b4822e51d756494bcc011193ea9187caa9a1913bdb5d8ae4f9550b7bc88a08bc165df0114e347c272555b383081d35d6a000cb41a0996f7e9db8e2797c62ae017fdf63b023508828f390482319050b7cdc8f96f1bd6167fe01274b26013e4c63c0e8e4c15f964dc3af2eb2d90695e52a84039c87bc1aa5683dd365059b6e084dbf2089079f9af1e3e6096b3c91e589271436687dbe5a96007a9c2a74a05dc2da9a545baa6cc2b52bc62685fe5b302e";
//        String url = SocketManager.getClassroomUrl(this,ticket);
//
//        String forceStr =  "true";
//        IO.Options opts = new IO.Options();
//        opts.query = new StringBuilder("secret=")
//                .append(secret)
//                .append("&avc={\"video\":")
//                .append(true)
//                .append(",\"audio\":")
//                .append(true)
//                .append("}&forcibly=")
//                .append(forceStr)
//                .toString();
//        //opts.timeout = TIME_OUT; //ms
//        opts.transports = new String[]{"websocket"};
//
//
//        try {
//            SocketManager.getSocketManager(this).connect(url,opts);
//
//            EventEmitor.claimStreaming(this, Live.StreamMode.AV,
//                    new EventCallback<ClaimReponse>() {
//                        @Override
//                        public void onSuccess(ClaimReponse claimReponse) {
//
//                            imageView.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onFailed(String errorCode, EventResponse response) {
//
//                            imageView.setVisibility(View.GONE);
//                        }
//                    });
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        String ticket = "e5339de8e921dce21724bd35234287469a97a18a85d98255cfd81277928e11dbeddeeab6469add1ffa596f3fdea3a68ce574686981b9180d6f8b4c4507845d45";
//
//        Duration schedule = new Duration();
//        schedule.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
//        schedule.setEnd(new Date(System.currentTimeMillis()+(3600*1000*24)));
//
//        Criteria criteria = new Criteria();
//        criteria.setDuration(schedule);
//
//        Pagination pagination = new Pagination();
//        pagination.setMaxNumOfObjectsPerPage(10);
//
//        LiveManager.getLiveSchedule(this, ticket, criteria, pagination, new APIServiceCallback<ResponseBody>() {
//            @Override
//            public void onSuccess(ResponseBody object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        String start = "2014-05-25T16:00:00.000Z";
//        String end = "2080-05-25T16:00:00.000Z";
//
//        LessonDataManager.getClassesSchedule4Class(this, start, end, "StandaloneLesson", "All", 100, 1, new APIServiceCallback<ScheduleData>() {
//            @Override
//            public void onSuccess(ScheduleData object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        String classId = "5927f85a9b2ba6cf531f1e75";
//
//        Intent i = new Intent(this, ClassInfoActivity.class);
//        i.putExtra(ClassInfoActivity.EXTRA_CLASSID, classId);
//
//        startActivity(i);

//        long start = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
//
//        long end = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
//
//
//        LessonDataManager.getClassesSchedule(this, start, end, new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });


//        LessonDataManager.getClassesSchedule(this, "daily", 1,1, new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        Pagination pagination = new Pagination();
//        pagination.setMaxNumOfObjectsPerPage(100);
//        pagination.setPage(1);
//
//        AccountDataManager.getOrgTeachers(this, "592258e34dd12305d0146cdb", false, pagination, new APIServiceCallback<CollectionPage<OrgTeacher>>() {
//            @Override
//            public void onSuccess(CollectionPage<OrgTeacher> object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        Intent i = new Intent(this, UpgradeActivity.class);
//        startActivity(i);

//        Intent i = new Intent(this, UpdateService.class);
//        i.putExtra(DConstants.EXTRA_URL,"http://file.vipkid.com.cn/apps/vipkid_v1.2.1.apk");
//        startService(i);

        //WechatUtil.shareWebpage(this,iwxapi,"小教室","小教室，大世界","http://sports.qq.com/nba/?ptag=baidu.ald.sc.nba",false);
        //WechatUtil.shareText(iwxapi,"小教室，大世界", false);

//        DownloadManager.enqueueDownload(this,
//                "Vipkid.apk",
//                "testkey-23esefs",
//                "http://file.vipkid.com.cn/apps/vipkid_v1.2.1.apk",
//                null,
//                null);
    }

    private void testT(Context context){

//        SearchManager.searchAccountsOrLessons(context, "185", "3", new APIServiceCallback<ResponseBody>() {
//            @Override
//            public void onSuccess(ResponseBody object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        String chaanel = APPUtils.getChannel(context);
//        Logger.d("---chaanel:"+ chaanel);

        //QQUtil.share(this,tencent,null);
        //WechatUtil.shareWebpage(this,iwxapi,"小教室","小教室，大世界","http://sports.qq.com/nba/?ptag=baidu.ald.sc.nba",true);
        //WechatUtil.shareText(iwxapi,"小教室，大世界", true);
//        DownloadManager.enqueueDownload(this,"pp" + System.currentTimeMillis(),
//                "key",
//                "http://img3.imgtn.bdimg.com/it/u=4271053251,2424464488&fm=23&gp=0.jpg",null,null);








//        String t = "94bf60841b32192ed0df1a8e025f6c0a08bd6f1cc753610fe4d55080c34da460b15feffa68840f4e045fad946a416675";
//        LiveManager.bootSession(getApplicationContext(), t, new APIServiceCallback<CtlSession>() {
//            @Override
//            public void onSuccess(CtlSession object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        CategoriesManager.requestGetSubject(context, new APIServiceCallback<CSubject>() {
//            @Override
//            public void onSuccess(CSubject object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });


//        Pagination pagination = new Pagination();
//        pagination.setPage(1);
//        pagination.setMaxNumOfObjectsPerPage(20);
//
//        SocialManager.getUpdates(context, pagination, new APIServiceCallback<CollectionPage<DynUpdate>>() {
//            @Override
//            public void onSuccess(CollectionPage<DynUpdate> object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        String cid = "5863a7de65894305bd4e8505";
//        String reply = "r2=======";
//
//        SocialManager.reply2Reply(context, cid, reply, new APIServiceCallback<Comment>() {
//            @Override
//            public void onSuccess(Comment object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        SocialManager.replyComment(context, cid, reply, new APIServiceCallback<Comment>() {
//            @Override
//            public void onSuccess(Comment object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        String activity = "5863662d0f56a24e3c635c58";
//        SocialManager.likeActivity(context, activity, new APIServiceCallback<Dynamic.DynStatus>() {
//            @Override
//            public void onSuccess(Dynamic.DynStatus object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });


//        Doc doc = new Doc();
//        doc.subtype = Social.ActivityType.POST_ACTIIVTY;
//        doc.id = "586374cdb08097313c1d602b";
//
//        Criteria criteria = new Criteria();
//        criteria.setDoc(doc);
//
//        Pagination pagination = new Pagination();
//        pagination.setPage(1);
//        pagination.setMaxNumOfObjectsPerPage(20);
//
//        SocialManager.getComments(context, criteria, pagination, new APIServiceCallback<CollectionPage<Comment>>() {
//            @Override
//            public void onSuccess(CollectionPage<Comment> object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });



//        String actvity = "586374cdb08097313c1d602b";
//        String comment = "我喜欢~~~";
//        SocialManager.commentActivity(context, actvity, comment, new APIServiceCallback<Comment>() {
//            @Override
//            public void onSuccess(Comment object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });



//        Duration duration = new Duration();
//        duration.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
//        duration.setEnd(new Date(System.currentTimeMillis()+(3600*1000*24)));
//
//        Criteria criteria = new Criteria();
//        criteria.setDuration(duration);
//
//        Pagination pagination = new Pagination();
//        pagination.setPage(1);
//        pagination.setMaxNumOfObjectsPerPage(20);
//
//
//        SocialManager.getActivities(context, criteria, pagination, new APIServiceCallback<CollectionPage<Dynamic>>() {
//            @Override
//            public void onSuccess(CollectionPage<Dynamic> object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        DynPost post = new DynPost();
//        post.text = "这是第一条动态";
//
//        SocialManager.postActivityRequest(context, post, new APIServiceCallback<Dynamic>() {
//            @Override
//            public void onSuccess(Dynamic object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

//        SocialManager.addContactGroupRequest(context, "MAX", new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });



//         Intent i = new Intent(this,PostDynamicActivity.class);
//        startActivity(i);





//            SecurityManager.requestHavePrivilege(context, new APIServiceCallback<Privilege[]>() {
//                @Override
//                public void onSuccess(Privilege[] object) {
//
//                }
//
//                @Override
//                public void onFailure(String errorCode, String errorMessage) {
//
//                }
//            }, Su.Permission.COURSE_OPEN_CREATE);


        //loginP.login(context,loginParams,null);

//        AccountRequest<HomeData> accountRequest = new AccountRequest<>(context, new APIServiceCallback<HomeData>() {
//            @Override
//            public void onSuccess(HomeData object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });
//
//        accountRequest.getHomeData("TG_yZafXu5EvI9BeOGCfeDcVK-4b65wP");


    }


    private void testLoading() {
        ProgressHUD.create(this).show();
    }

    private void testGetData(Context context) {

        String lesson = "5834f31eaf662a8111362161";

        LessonDataManager.requestLessonData(context, lesson, new APIServiceCallback<LessonDetail>() {
            @Override
            public void onSuccess(LessonDetail object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

    }

    private void testCenterData(Context context) {

        AccountDataManager.requestCenterData(context, new APIServiceCallback<CenterData>() {
            @Override
            public void onSuccess(CenterData object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

    }

    private void crash(){


        CrashReport.testJavaCrash();
    }

    private void anr(){


        CrashReport.testANRCrash();
    }

    private void testHUB(Context context) {

        ProgressHUD.create(context).show();

    }

    private void confirmEnllor(Context context) {


        String lesson = "5832adb511efdb0a38faffe1";
        String re = "5832aeaa11efdb0a38fafff1";

        LessonDataManager.requestLessonEnrollment(context, lesson, re, new APIServiceCallback<CLEResponse>() {
            @Override
            public void onSuccess(CLEResponse object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }


    private void testEnrollLesson(Context context){

        String lesson= "582d58353d73a5046fc24490";
//        LessonDataManager.requestEnrollLesson(context, sessionid, lesson, new APIServiceCallback<ELResponse>() {
//            @Override
//            public void onSuccess(ELResponse object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });
    }


    private void testEditLesson(Context context) {

//        String lesson= "582d58353d73a5046fc24490";
//        Enroll enroll = new Enroll();
//        enroll.setMax(100);
//        enroll.setMandatory(false);
//
//        Fee fee = new Fee();
//        fee.setFree(false);
//        fee.setType(Finance.PricingType.TOTAL);
//        fee.setCharge(100);
//
//        Schedule sch = new Schedule();
//        sch.setStart(new Date(System.currentTimeMillis()));
//        sch.setDuration(1000);
//
//
//        LiveLesson ll = new LiveLesson();
//        ll.setTitle("无人驾驶课");
//        //ll.setSubject(subject);
//        ll.setEnroll(enroll);
//        ll.setMode(Ctl.TeachingMode.ONE_2_ONE);
//        ll.setFee(fee);
//        ll.setSchedule(sch);
//
//        LessonDataManager.requestEditLesson(context, lesson, ll, new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });


    }

    private void testLessonHomePage(Context context) {

//        String lesson = "5829530cd82a272d115ae6bc";
//        LessonDataManager.requestLessonHomepage(context, lesson, new APIServiceCallback<LessonDetail>() {
//            @Override
//            public void onSuccess(LessonDetail object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

    }

    private void testCoverUpLoad(Context context ) {





    }


    private void editCover() {
        Intent i = new Intent(this, CropImageMainActivity.class);
        startActivityForResult(i, CROP_PORTRAIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (data != null) {
                    String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                    Bitmap portrait = BitmapFactory.decodeFile(cropImgPath);
                    if (portrait != null) {



//                        LessonDataManager.requestUploadCover(TestAPIActivity.this,"",cropImgPath,new QiniuService(){
//
//                            @Override
//                            public void uploadSuccess(String fileName, String fileUrl) {
//
//                            }
//
//                            @Override
//                            public void uploadFailure() {
//
//                            }
//                        });

//                        AccountDataManager.requestUploadAvatar(TestAPIActivity.this, sessionid, id, cropImgPath, new QiniuService() {
//                            @Override
//                            public void uploadSucess(String fileName,String fileUrl) {
//
//
//
//                            }
//
//                            @Override
//                            public void uploadFailure() {
//
//
//
//                            }
//                        });

                    }
                }
                break;
        }
    }



    private void getProfile(Context context) {

//        AccountDataManager.requestProfile(context, new APIServiceCallback<Account.Basic>() {
//            @Override
//            public void onSuccess(Account.Basic object) {
//
//            }
//
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });
    }

    private void editProfile(Context context) {
        Account.Basic b = new Account.Basic();
        b.setBirthday(new Date(System.currentTimeMillis()));
        b.setSex("false");
        b.setTitle("天下唯我独尊");

        AccountDataManager.requestEditProfile(context, b, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void testGetUpToken(Context context) {

//        AccountDataManager.requestAvatorUpToken(context, sessionid,new APIServiceCallback<String>() {
//            @Override
//            public void onSuccess(String object) {
//                token = object;
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

    }

    //getSubject demo
    private void testGetSubject(Context context) {

        CategoriesManager.requestGetSubject(context, new APIServiceCallback<CSubject>() {
            @Override
            public void onSuccess(CSubject object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void testJsonFormat() {
        Duration duration = new Duration();
        duration.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
        duration.setEnd(new Date(System.currentTimeMillis()));

        Criteria criteria = new Criteria();
        criteria.setSource(Ctl.LessonSource.ALL);
        criteria.setDuration(duration);

//        ServiceRequest serviceRequest = new ServiceRequest();
//
//        String jsonstr = serviceRequest.objectToJsonString(criteria);
//        Logger.json(jsonstr);
    }

    private void testPutLessonOnShelves(Context context) {

        String lession = "58211abfc52b32f4568faa58";

        LessonDataManager.requestPutLessonOnShelves(context, lession, new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                Logger.d("onFailure-----------");
            }
        });
    }

    //获取已开的课
    private void testGetLessons(Context context) {

        Duration duration = new Duration();
        duration.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
        duration.setEnd(new Date(System.currentTimeMillis()));

        Criteria criteria = new Criteria();
        criteria.setSource(Ctl.LessonSource.ALL);
        criteria.setDuration(duration);


        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(20);


//        LessonDataManager.requestGetLessons(context, criteria, pagination, new APIServiceCallback<GetLessonsResponse>() {
//            @Override
//            public void onSuccess(GetLessonsResponse object) {
//                Logger.d("onSuccess-----------");
//            }
//
//            @Override
//            public void onFailure(String errorCode,String errorMessage) {
//
//                Logger.d("onFailure-----------");
//            }
//        });
    }



    //创建直播课
    private void testCreateLession(Context context) {
//
//        Enroll enroll = new Enroll();
//        enroll.setMax(100);
//        enroll.setMandatory(false);
//
//        Fee fee = new Fee();
//        fee.setFree(true);
//        fee.setType(Finance.PricingType.TOTAL);
//        fee.setCharge(100);
//
//        Schedule sch = new Schedule();
//        sch.setStart(new Date(System.currentTimeMillis()));
//        sch.setDuration(1000);
//
//
//        LiveLesson ll = new LiveLesson();
//        ll.setTitle("无人驾驶课");
//        //ll.setSubject(subject);
//        ll.setEnroll(enroll);
//        ll.setMode(Ctl.TeachingMode.ONE_2_ONE);
//        ll.setFee(fee);
//        ll.setSchedule(sch);
//
//        CreateLesson cl = new CreateLesson();
//        cl.setData(ll);
//
//
//        LessonDataManager.requestCreateLiveLesson(context, cl, new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//                Logger.d("onSuccess-----------");
//            }
//
//            @Override
//            public void onFailure(String errorCode,String errorMessage) {
//                Logger.d("onFailure-----------");
//            }
//        });


    }

    //声明教学能力
    private void testClaimCompetency(Context context) {


        CompetencyParams cp = new CompetencyParams();
        cp.setSubject(subject);


//        AccountDataManager.requestClaimCompetency(context, cp, new APIServiceCallback<ClaimCompetency>() {
//            @Override
//            public void onSuccess(ClaimCompetency object) {
//                Logger.d("onSuccess-----------");
//            }
//
//            @Override
//            public void onFailure(String errorCode,String errorMessage) {
//
//                Logger.d("onFailure-----------");
//            }
//        });

    }


    //退出
    private void testLogout(Context context) {


        LoginDataManager.requestLogoutByAPI(context, new APIServiceCallback() {

            @Override
            public void onSuccess(Object object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Logger.d("onFailure-----------");
            }
        });

    }

    //登陆
    private void testLogin(final Context context) {

//        LoginParams params = new LoginParams();
//        params.setPassword("123456");
//        params.setCt(Security.CredentialType.PERSION);
//        params.setMobile(mob);
//
//        LoginDataManager.requestLoginByAPI(context, params, new APIServiceCallback<LoginInfo>() {
//            @Override
//            public void onSuccess(LoginInfo object) {
//
//                Logger.d("onSuccess-----------");
//
//                sessionid = object.getUser().getSessionID();
//
//                Toast.makeText(context, "login Ok:" + sessionid, Toast.LENGTH_LONG).show();
//
//            }
//
//            @Override
//            public void onFailure(String errorCode,String errorMessage) {
//
//                Logger.d("onFailure-----------");
//            }
//        });
    }


    //注册
    private void testRegister(final Context context) {

//        RegisterInfo registerInfo = new RegisterInfo();
//        registerInfo.setCode(vcode);
//        registerInfo.setMobile(mob);
//        registerInfo.setPassword("123456");
//        registerInfo.setUsername("m_test1");
//
//        RegisterDataManager.requestRegisterByAPI(context, registerInfo, new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//
//                Logger.d("onSuccess-----------");
//                Toast.makeText(context, "register Ok", Toast.LENGTH_LONG).show();
//
//            }
//
//            @Override
//            public void onFailure(String errorCode,String errorMessage) {
//                Logger.d("onFailure-----------register");
//                Toast.makeText(context, "register error code:" + errorCode, Toast.LENGTH_LONG).show();
//            }
//        });

    }

    private void testValidateCode(final Context context) {
        RegisterDataManager.requestValidateCode(context, mob, vcode, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                Toast.makeText(context, "validate Ok", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Toast.makeText(context, "validate error code:" + errorCode, Toast.LENGTH_LONG).show();
            }
        });

    }

    //发送验证码
    private void testSendCode(final Context context) {

        RegisterDataManager.requestSendVerifyCode(context, mob, new APIServiceCallback<VerifyCode>() {
            @Override
            public void onSuccess(VerifyCode object) {
                Logger.d("onSuccess-----------");

                vcode = object.getCode();
                Toast.makeText(context, "code:" + vcode, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Logger.d("onFailure-----------errorcode:%s", errorCode);
            }
        });

    }

}
