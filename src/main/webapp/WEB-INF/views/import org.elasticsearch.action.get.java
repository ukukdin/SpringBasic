import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class FinancialAccidentRepositoryImpl implements FinancialAccidentRepository {

    private static final Logger logger = LoggerFactory.getLogger(FinancialAccidentRepositoryImpl.class);
    private final RestHighLevelClient client;

    @Autowired
    public FinancialAccidentRepositoryImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public List<Map<String, Object>> searchFinancialAccidents(String accidentGroupId, String startDate, String startTime, String endDate, String endTime, int pageSize, int offset) {
        List<Map<String, Object>> financialAccidentList = new ArrayList<>();
        try {
            BoolQueryBuilder filter = QueryBuilders.boolQuery()
                    .must(QueryBuilders.rangeQuery(CommonConstants.CALLCENTER_COMMENT_FIELD_NAME_FOR_LOG_DATE_TIME)
                            .gte(startDate + " " + startTime)
                            .lte(endDate + " " + endTime))
                    .must(QueryBuilders.termQuery(FdsMessageFieldNames.IS_FINANCIAL_ACCIDENT, "Y"))
                    .must(QueryBuilders.termQuery(FdsMessageFieldNames.FINANCIAL_ACCIDENT_GROUP_ID, accidentGroupId));

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).filter(filter))
                    .from(offset).size(pageSize)
                    .sort(FdsMessageFieldNames.LOG_DATE_TIME, SortOrder.DESC)
                    .trackTotalHits(true);

            SearchRequest searchRequest = new SearchRequest().source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            for (var hit : hits) {
                Map<String, Object> document = new HashMap<>(hit.getSourceAsMap());
                document.put("indexName", hit.getIndex());
                document.put("docId", hit.getId());
                financialAccidentList.add(document);
            }
        } catch (IOException e) {
            logger.error("Error searching financial accidents", e);
        }
        return financialAccidentList;
    }

    @Override
    public List<Map<String, Object>> retrieveFinancialAccidentsByIds(String[] ids) {
        List<Map<String, Object>> financialAccidentList = new ArrayList<>();
        try {
            for (String id : ids) {
                String[] searchEngineId = StringUtils.split(StringUtils.trimToEmpty(id), CommonConstants.SEPARATOR_FOR_SPLIT);
                if (searchEngineId.length < 3) continue;
                
                GetRequest getRequest = new GetRequest(searchEngineId[0], searchEngineId[2]);
                GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);

                if (response.isExists()) {
                    Map<String, Object> document = new HashMap<>(response.getSourceAsMap());
                    document.put("indexName", response.getIndex());
                    document.put("docId", response.getId());
                    financialAccidentList.add(document);
                }
            }
        } catch (IOException e) {
            logger.error("Error retrieving financial accidents by IDs", e);
        }
        return financialAccidentList;
    }
}
