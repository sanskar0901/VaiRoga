
// <!-- TODO: Add SDKs for Firebase products that you want to use
//      https://firebase.google.com/docs/web/setup#available-libraries -->
// <script src="https://www.gstatic.com/firebasejs/8.3.3/firebase-analytics.js"></script>





  // Your web app's Firebase configuration
  // For Firebase JS SDK v7.20.0 and later, measurementId is optional
  var firebaseConfig = {
    apiKey: "AIzaSyCvA-vSsTQDBO5TewpLZCZfLSDWKQhtDo4",
    authDomain: "vairoga-d0b49.firebaseapp.com",
    databaseURL: "https://vairoga-d0b49-default-rtdb.firebaseio.com",
    projectId: "vairoga-d0b49",
    storageBucket: "vairoga-d0b49.appspot.com",
    messagingSenderId: "379003828158",
    appId: "1:379003828158:web:d34e9e53e653cdd785ebef",
    measurementId: "G-XH66PJ47V5"
  };
  // Initialize Firebase
  firebase.initializeApp(firebaseConfig);
//   firebase.analytics();
const auth = firebase.auth();
const database =firebase.database();



 function register(){
    const emaill = document.getElementById('email');
    const namee = document.getElementById('user_name');
    
    const mobile = document.getElementById('mobile');
    const aadhar = document.getElementById('aadhar');
    var email = document.getElementById("email");
    var password = document.getElementById("password");
    console.log(email.value)
    console.log(password.value)
    firebase.auth().createUserWithEmailAndPassword(email.value, password.value) .then(function success(userData){
        // Signed in 
        var user = userData.user.uid;
        console.log(user);
        console.log(namee.value)
        // ...
        database.ref('/users/' + user +'/userData/').set({

          name: namee.value,
          email: emaill.value,
          mob: mobile.value,
          aadhar: aadhar.value,
          type: "PATIENT",
          credit: 100,
          uid: user

          
      })
      window.alert("signed up!!")
      window.location.assign("patients_login.html")

      
      .catch((error) => {
        var errorCode = error.code;
        var errorMessage = error.message;
        // ..
      });

     
 }
 )};

 function p_login() {
   console.log("huuu0");
    const email = document.getElementById('l-email');
    const pasword = document.getElementById('l-password');
    
    console.log("hi")
    firebase.auth().signInWithEmailAndPassword(email.value, pasword.value)
      .then((userCredential) => {
        // Signed in
        var user = userCredential.user;
        // ...
        
        var db = database.ref('/user/' + user.uid + '/userData/name');
        db.on('value',(snapshot)=>{
          const datas = snapshot.val();
          var list = document.getElementsByClassName("fb-name")
        for(var i =0;i<list.length;i++){
          list[i].innerHTML = datas;
        }
        });
        window.location.assign("../../patient_dashboard.html");
      })
      .catch((error) => {
        var errorCode = error.code;
        var errorMessage = error.message;
        console.log(email.value)
        console.log(errorCode);
        console.log(errorMessage);
      })
    };

function prescription(){
  const date = document.getElementById("Date");
  const symptoms = document.getElementById("Symptoms");
  const test = document.getElementById("Tests");
  var uid = firebase.auth().currentUser.uid;
  var date_node = date.value +"";
  console.log(uid);
  database.ref('/users/' + uid + '/userData/prescriptions/' + date_node).set({
    date: date.value,
    symptoms: symptoms.value,
    tests:test.value
  });
  
};
    
    




function dl_login_func(){
  console.log("hiihiiii")
  var dl_email = document.getElementById("dl_email")
  var dl_password = document.getElementById("dl_password")
  console.log(dl_email.value);
  console.log(dl_password.value);
  
  firebase.auth().signInWithEmailAndPassword(dl_email.value ,dl_password.value)
  .then((userCredential)=>{
    window.location.assign("../../dashboard.html");

    
  })
  .catch((error)=>{
    var errorCode = error.code;
    var errorMessage = error.message;
    console.log(errorCode);
    console.log(errorMessage);
  });
};

function logout(){
  console.log("huhuhuhuhu");
  firebase.auth().signOut().then(()=>{
    window.location.assign("./index.html")
  })
};

firebase.auth().onAuthStateChanged(function (user) {
  if (user!=null) {
    console.log(user.uid);
    var db = firebase.database().ref('users/' + user.uid + '/userData/name');
    db.on('value',(snapshot) =>{
      const datas = snapshot.val();
      console.log(datas);
          var list = document.getElementsByClassName("fb_name");
        for(var i =0;i<list.length;i++){
          list[i].innerHTML = datas;
          
        }
    });
    var db_em = firebase.database().ref('users/' + user.uid + '/userData/email');
    db_em.on('value',(snapshot)=>{
      const edatas = snapshot.val();
      document.getElementById("fb_email").innerHTML = edatas;
    });
  }
   else;
  
});


// Calls the onClick command from the Populate Button
function search() {

// Creates a new Firebase reference linking this JS to the Firebase database
var ref = new Firebase("https://vairoga-d0b49-default-rtdb.firebaseio.com/users/gqFK1fQ98PR0trsr1OiPHs5Dksn2/userData/aadhar");

// Creates a snapshot of the data from the desired Firebase database and adds it to 'value' 
ref.on('value', function(snapshot) {

// Adds a console log of the data recived from Firebase
console.log(snapshot.val());

// Creates a variable called data to which the snapshot is saved
var data = snapshot.val();

for (var key in data) {
  
  if (data.hasOwnProperty(key)) {

    // Takes the id of the textbox and adds a value of (data[key]) where data = snapshot.val() which is the snapshot of the data
    document.getElementById('id1').value=(data[key]);

    // Adds a console log message to confirm the textbox has been populated
    console.log('Value Added');
    };
    // End of if
  }
  // End of for
});
// End of ref.on
}
// End of function



function getMedicalHistory(){
  var cuser = firebase.auth().currentUser.uid;
  var db = firebase.database().ref('/users/' + cuser + '/userData/prescriptions');
  db.on('value',(snapshot)=>{
    snapshot.forEach(function(childSnapshot){
      var date = childSnapshot.key;
      var symptoms = childSnapshot.val();
      console.log(symptoms);
      var date_db = symptoms['date'];
      var symptoms_db = symptoms['symptoms'];
      var tests = symptoms['tests'];
      document.getElementById("Tests").innerHTML = `Date Prescribed: ${date_db}
Symptoms Discovered: ${symptoms_db}
Tests Given: ${tests}`
    });
  });
}