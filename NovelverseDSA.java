import java.io.*;
import java.util.*;

/* ================= BOOK ================= */
class Book implements Serializable{
    String title;
    String author;
    int totalPages;
    int currentPage;
    boolean favourite;
    boolean wantToRead;

    Book(String t, String a, int pages) {
        title = t;
        author = a;
        totalPages = pages;
        currentPage = 0;
    }

    int progress() {
        if (totalPages == 0) return 0;
        return (int)((currentPage * 100.0) / totalPages);
    }
}

/* ================= USER ================= */
class User implements Serializable{
    String username;
    String password;

    ArrayList<Book> books = new ArrayList<>();
    HashSet<Book> favourites = new HashSet<>();
    ArrayList<String> friends = new ArrayList<>();
    Queue<String> requests = new LinkedList<>();

    User(String u, String p) {
        username = u;
        password = p;
    }
}

/* ================= APP ================= */
public class NovelverseDSA {

    static Scanner sc = new Scanner(System.in);
    static HashMap<String, User> users = new HashMap<>();
    static User currentUser = null;

    public static void main(String[] args) {
loadUsers();
        while (true) {
            if (currentUser == null) authMenu();
            else homeMenu();
        }
    }

    /* ================= AUTH ================= */
    static void authMenu() {
        System.out.println("\n=== Novelverse ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");

        int ch = sc.nextInt(); sc.nextLine();

        if (ch == 1) register();
        else if (ch == 2) login();
        else System.exit(0);
    }

    static void register() {
        System.out.print("Username: ");
        String u = sc.nextLine();
        System.out.print("Password: ");
        String p = sc.nextLine();

        if (users.containsKey(u)) {
            System.out.println("User exists!");
            return;
        }

        users.put(u, new User(u,p));
saveUsers();
System.out.println("Account created!");
    }

    static void login() {
        System.out.print("Username: ");
        String u = sc.nextLine();
        System.out.print("Password: ");
        String p = sc.nextLine();

        if (users.containsKey(u) && users.get(u).password.equals(p)) {
            currentUser = users.get(u);
            System.out.println("Welcome " + u + "!");
        } else {
            System.out.println("Invalid login");
        }
    }

    /* ================= HOME ================= */
    static void homeMenu() {
        System.out.println("\n--- Home ---");
        System.out.println("1. Dashboard");
        System.out.println("2. Add Book");
        System.out.println("3. View Books");
        System.out.println("4. Favourites");
        System.out.println("5. Want To Read");
        System.out.println("6. Search Book");
        System.out.println("7. Friends");
        System.out.println("8. Notifications");
        System.out.println("9. Logout");

        int ch = sc.nextInt(); sc.nextLine();

        switch (ch) {
            case 1 -> dashboard();
            case 2 -> addBook();
            case 3 -> viewBooks();
            case 4 -> showFavourites();
            case 5 -> showWantList();
            case 6 -> searchBook();
            case 7 -> friendsMenu();
            case 8 -> notifications();
            case 9 -> currentUser = null;
        }
    }
static void saveUsers() {
    try {
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("users.dat"));
        out.writeObject(users);
        out.close();
    } catch (Exception e) {
        System.out.println("Error saving users");
    }
}

@SuppressWarnings("unchecked")
static void loadUsers() {
    try {
        ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("users.dat"));
        users = (HashMap<String, User>) in.readObject();
        in.close();
    } catch (Exception e) {
        users = new HashMap<>();
    }
}
    /* ================= DASHBOARD ================= */
    static void dashboard() {
        long reading = currentUser.books.stream().filter(b -> b.progress()<100 && !b.wantToRead).count();
        long completed = currentUser.books.stream().filter(b -> b.progress()==100).count();
        long want = currentUser.books.stream().filter(b -> b.wantToRead).count();

        System.out.println("\nTotal Books: " + currentUser.books.size());
        System.out.println("Reading: " + reading);
        System.out.println("Completed: " + completed);
        System.out.println("Want to read: " + want);
    }

    /* ================= ADD BOOK ================= */
   static void addBook() {
    System.out.print("Title: ");
    String t = sc.nextLine();

    // check duplicates
    for(Book b : currentUser.books){
        if(b.title.equalsIgnoreCase(t)){
            System.out.println("Book already exists.");
            return;
        }
    }

    System.out.print("Author: ");
    String a = sc.nextLine();

    System.out.print("Total Pages: ");
    int p = sc.nextInt(); sc.nextLine();

    currentUser.books.add(new Book(t,a,p));
    saveUsers();

    System.out.println("Book added!");
}

    /* ================= VIEW BOOKS ================= */
    static void viewBooks() {
        if (currentUser.books.isEmpty()) {
            System.out.println("No books added.");
            return;
        }

        for (int i=0;i<currentUser.books.size();i++) {
            Book b = currentUser.books.get(i);
            System.out.println((i+1)+". "+b.title+
                    " ("+b.progress()+"%)");
        }

        System.out.println("Select book to update (0 to exit): ");
        int ch = sc.nextInt(); sc.nextLine();
        if (ch==0) return;

        Book b = currentUser.books.get(ch-1);

        System.out.println("1.Update Progress  2.Toggle Favourite  3.Want to Read");
        int op = sc.nextInt(); sc.nextLine();

        if(op==1){
            System.out.print("Current page: ");
            b.currentPage = sc.nextInt();
        }
        else if(op==2){
            b.favourite=!b.favourite;
            if(b.favourite) currentUser.favourites.add(b);
            else currentUser.favourites.remove(b);
        }
        else if(op==3){
            b.wantToRead=!b.wantToRead;
        }
    }

    /* ================= FAVOURITES ================= */
    static void showFavourites(){
        currentUser.favourites.forEach(b->System.out.println(" "+b.title));
    }

   static void showWantList(){

    boolean found = false;

    for(Book b : currentUser.books){
        if(b.wantToRead){
            System.out.println("📖 " + b.title);
            found = true;
        }
    }

    if(!found){
        System.out.println("Nothing is there to show.");
    }
}

    /* ================= SEARCH ================= */
   static void searchBook(){
    System.out.print("Search title: ");
    String key = sc.nextLine().toLowerCase();

    boolean found = false;

    for(Book b : currentUser.books){
        if(b.title.toLowerCase().contains(key)){
            System.out.println("Found: " + b.title);
            found = true;
        }
    }

    if(!found){
        System.out.println("Book not found.");
    }
}
    /* ================= FRIENDS ================= */
    static void friendsMenu(){
        System.out.println("1.Send Request 2.View Friends");
        int ch = sc.nextInt(); sc.nextLine();

        if(ch==1){
            System.out.print("Enter username: ");
            String name = sc.nextLine();

            if(!users.containsKey(name)){
    System.out.println("User not found.");
}
else if(name.equals(currentUser.username)){
    System.out.println("You cannot add yourself.");
}
else{
    users.get(name).requests.add(currentUser.username);
    System.out.println("Request sent!");
}
        } if(currentUser.friends.isEmpty()){
    System.out.println("No friends :( Send requests?");
} else {
    currentUser.friends.forEach(f -> System.out.println(" " + f));
}
    }

    /* ================= NOTIFICATIONS ================= */
    static void notifications(){

    if(currentUser.requests.isEmpty()){
        System.out.println("No notifications");
        return;
    }

    while(!currentUser.requests.isEmpty()){

        String req = currentUser.requests.poll();  // ✅ req defined here

        System.out.println(req + " sent you a friend request. Accept? (yes/no)");
        String ans = sc.nextLine();

        if(ans.equalsIgnoreCase("yes")){
            currentUser.friends.add(req);
            users.get(req).friends.add(currentUser.username);
            saveUsers();   // optional but recommended
            System.out.println("You are now friends!");
        }
    }
}
}