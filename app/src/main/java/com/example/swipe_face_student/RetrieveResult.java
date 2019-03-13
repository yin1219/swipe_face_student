package com.example.swipe_face_student;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.swipe_face_student.Model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RetrieveResult extends AppCompatActivity {

    TextView name, id, email, department, school;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//抓現在登入user
    String email1 = user.getEmail();//抓user.email
    String [] uriEmailArray = email1.split("@");
    String uriEmail = uriEmailArray[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_result);


        id = (TextView)findViewById(R.id.result_id);
        name = (TextView)findViewById(R.id.result_name);
        department = (TextView)findViewById(R.id.result_email);
        school = (TextView)findViewById(R.id.result_school);
        email = (TextView)findViewById(R.id.result_email);
        Query query = db.collection("Student")
                .whereEqualTo("student_id",uriEmail);
        query.get().addOnCompleteListener(task ->{
            QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult():null ;

            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                Student student = documentSnapshot.toObject(Student.class);
                id.setText(student.getStudent_id());
                name.setText(student.getStudent_name());
                department.setText(student.getStudent_department());
                school.setText(student.getStudent_school());
                email.setText(student.getStudent_email());

            }
        });


    }
}
