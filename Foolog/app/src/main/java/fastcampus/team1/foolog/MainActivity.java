package fastcampus.team1.foolog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fastcampus.team1.foolog.Chart.CircleChartActivity;
import fastcampus.team1.foolog.Map.MapsActivity;
import fastcampus.team1.foolog.util.PermissionControl;
import me.huseyinozer.TooltipIndicator;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PermissionControl.CallBack {

    private TooltipIndicator indicator;
    TextView txtNavi_Email, txtNavi_nickName;
    String email, nick;
    NavigationView navigationView;
    Fragment[] arr;
    ViewPager viewPager;
    MyPagerAdapter adapter;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    Context mContext = this;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        setFragment();
        setAdapter();
        setNaviView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), WriteActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initView(){
        // 앱 실행시 권한을 사용하게끔 나타낸다.
        PermissionControl.checkVersion(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (TooltipIndicator) findViewById(R.id.tooltip_indicator);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setFragment(){
        arr = new Fragment[2];
        arr[0] = CalendarFragment.newInstance(mContext);
        arr[1] = new ListFragment();
    }

    public void setAdapter(){
        adapter = new MyPagerAdapter(getSupportFragmentManager(), arr);
        viewPager.setAdapter(adapter);
        indicator.setupViewPager(viewPager);
    }

    public void getPreferences(){
        SharedPreferences storage = getSharedPreferences("storage", Activity.MODE_PRIVATE);
        email = storage.getString("inputEmail", " ");
        nick = storage.getString("inputNickName"," ");
    }

    public void setNaviView(){
        getPreferences();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtNavi_Email = (TextView) headerView.findViewById(R.id.navNickname);
        txtNavi_nickName = (TextView) headerView.findViewById(R.id.navEmail);
        txtNavi_nickName.setText(email);
        txtNavi_Email.setText(nick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getBaseContext(), WriteActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_circle_graph) {
            Intent intent = new Intent(this, CircleChartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_graph) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            SharedPreferences storage = getSharedPreferences("storage", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = storage.edit();
            //editor.clear()는 storage 에 들어있는 모든 정보를 기기에서 지움
            editor.clear();
            editor.commit();
            Toast.makeText(MainActivity.this, "로그아웃.", Toast.LENGTH_SHORT).show();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 권한 요청을 위한 함수
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionControl.onResult(this, requestCode, grantResults);
    }

    /**
     * 권한 설정을 위한 callback함수 (interface)
     * 만약 권한을 설정을 해준다면 버튼이 동작하게 만들어준다.
     * todo 처음에는 실행이 잘 되지만 껏다가 다시 키면 에러가 뜬다
     */

    @Override
    public void init() {

    }
}
