package easy.life.sharuash.ui.login;

import static android.content.Context.MODE_PRIVATE;

import java.util.ArrayList;
import java.util.List;
import easy.life.sharuash.DataClass;
import easy.life.sharuash.MyAdapter;
import easy.life.sharuash.R;
import easy.life.sharuash.UploadActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home2 extends Fragment {

    List<DataClass> dataList;
    MyAdapter adapter;
    SearchView searchView;
    Activity context;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.fragment_home2, container, false);

    }

   public void onStart(){
        super.onStart();
       context = getActivity();
       String net = getString(R.string.net);
       String net2 = getString(R.string.net2);
       recyclerView = (RecyclerView) context.findViewById(R.id.recyclerView123);
       searchView = (SearchView) context.findViewById(R.id.search);
       searchView.clearFocus();
       GridLayoutManager gridLayoutManager = new GridLayoutManager(Home2.this.context, 1);
       recyclerView.setLayoutManager(gridLayoutManager);

       if (isNetworkConnected()) {

           AlertDialog.Builder builder = new AlertDialog.Builder(Home2.this.context);
           builder.setCancelable(false);
           builder.setView(R.layout.progress_layout);
           AlertDialog dialog = builder.create();
           dialog.show();
           dataList = new ArrayList<>();
           adapter = new MyAdapter(Home2.this.context, dataList);
           recyclerView.setAdapter(adapter);

           SharedPreferences pref = context.getSharedPreferences("Phone number", MODE_PRIVATE);
           String phoneNumber = pref.getString("Phone number kz", "");
           databaseReference = FirebaseDatabase.getInstance().getReference(phoneNumber);
           dialog.show();
           eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   dataList.clear();
                   for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                       DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                       dataClass.setKey(itemSnapshot.getKey());
                       dataList.add(dataClass);
                   }
                   adapter.notifyDataSetChanged();
                   dialog.dismiss();

               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   dialog.dismiss();
                   Toast.makeText(context, net2 + error.getMessage(), Toast.LENGTH_SHORT).show();
               }
           });

   } else {
        Toast.makeText(context, net, Toast.LENGTH_SHORT).show();
    }

           searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextSubmit(String query) {
                   return false;
               }

               @Override
               public boolean onQueryTextChange(String newText) {
                   searchList(newText);
                   return true;
               }
           });

           FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.animalBtn);
           fab.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(context, UploadActivity.class);
                   startActivity(intent);
               }
           });


    }
    public void searchList(String text){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass: dataList){
            if (dataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

       private boolean isNetworkConnected() {
           ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo Network = cm.getActiveNetworkInfo();
           return Network != null && Network.isConnected();
       }
}
