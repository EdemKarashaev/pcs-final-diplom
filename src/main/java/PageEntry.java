public class PageEntry implements Comparable<PageEntry> {
    private String pdfName;
    private int page;
    private int count;

    public PageEntry(String fileName, int pageNumber, int count) {
        this.pdfName = fileName;
        this.page = pageNumber;
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry other) {
        return Integer.compare(this.count, other.count);
    }


    public String getPdf() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount(){count++;}


   public String toString() {
        return "File name: " + getPdf() + " ,page number: " + getPage() +" ,количество "+getCount();
    }
}
