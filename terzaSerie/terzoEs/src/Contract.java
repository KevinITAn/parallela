class Contract implements Runnable {
    private final int id;

    public Contract(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Contact" + id + ": started");

        int version = -1;
        while (true) {
            // Wait for version updates
            if (version == Businessman.version) {
                continue;
            }
            // Update local version
            version = Businessman.version;
            // Used to terminate
            if (version == -1) {
                break;
            }

            // update local numbers

            int home;
            int office;
            int mobile;
            int emergency;
            Businessman.lock.lock();
            try {
                home = Businessman.home;
                office = Businessman.office;
                mobile = Businessman.mobile;
                emergency = Businessman.emergency;
            } finally {
                Businessman.lock.unlock();
            }
            System.out.println("Contact" + id + ": new Phonenumbers [home=" + home + ", office=" + office + ", mobile=" + mobile + ", emergency=" + emergency + "]");
        }
        System.out.println("Contact" + id + ": terminating");
    }
}