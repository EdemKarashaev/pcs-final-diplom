import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface SearchEngine {

    List<PageEntry> search(String word) throws JsonProcessingException;
}
