@Repository
public class FinancialAccidentRepositoryImpl implements FinancialAccidentRepository {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public List<HashMap<String, Object>> searchFinancialAccidents(String accidentGroupId, String startDate, String startTime, String endDate, String endTime, int pageSize, int offset) {
        try {
            List<HashMap<String, Object>> financialAccidentList = new ArrayList<>();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder filter = QueryBuilders.boolQuery()
                    .must(QueryBuilders.rangeQuery(CommonConstants.CALLCENTER_COMMENT_FIELD_NAME_FOR_LOG_DATE_TIME)
                            .gte(startDate + " " + startTime)
                            .lte(endDate + " " + endTime))
                    .must(QueryBuilders.termQuery(FdsMessageFieldNames.IS_FINANCIAL_ACCIDENT, "Y"))
                    .must(QueryBuilders.termQuery(FdsMessageFieldNames.FINANCIAL_ACCIDENT_GROUP_ID, accidentGroupId));

            sourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).filter(filter));
            sourceBuilder.from(offset).size(pageSize);
            sourceBuilder.sort(FdsMessageFieldNames.LOG_DATE_TIME, SortOrder.DESC);
            sourceBuilder.trackTotalHits(true);

            SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH).source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT); // Use client.search()
            SearchHits hits = searchResponse.getHits();

            for (SearchHit hit : hits) {
                HashMap<String, Object> document = (HashMap<String, Object>) hit.getSourceAsMap();
                document.put("indexName", hit.getIndex());
                document.put("docType", hit.getType());
                document.put("docId", hit.getId());
                financialAccidentList.add(document);
            }
            return financialAccidentList;
        } catch (IOException e) {
            Logger.error("Error searching financial accidents", e); // Log the exception
            return new ArrayList<>(); // Or throw a custom exception
        }
    }

    @Override
    public List<HashMap<String, Object>> retrieveFinancialAccidentsByIds(String[] ids) {
        try {
            List<HashMap<String, Object>> financialAccidentList = new ArrayList<>();
            for (String id : ids) {
                String[] searchEngineId = StringUtils.split(StringUtils.trimToEmpty(id), CommonConstants.SEPARATOR_FOR_SPLIT);
                String indexName = searchEngineId[0];
                String docType = searchEngineId[1];
                String docId = searchEngineId[2];

                GetRequest getRequest = new GetRequest(indexName, docId);
                GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);

                HashMap<String, Object> document = (HashMap<String, Object>) response.getSourceAsMap();
                document.put("indexName", response.getIndex());
                document.put("docType", response.getType());
                document.put("docId", response.getId());
                financialAccidentList.add(document);
            }
            return financialAccidentList;
        } catch (IOException e) {
            Logger.error("Error retrieving financial accidents by IDs", e); // Log the exception
            return new ArrayList<>(); // Or throw a custom exception
        }
    }
}


3. 레포지토리 인터페이스 
public interface FinancialAccidentRepository {
    List<HashMap<String, Object>> searchFinancialAccidents(String accidentGroupId, String startDate, String startTime, String endDate, String endTime, int pageSize, int offset);
    List<HashMap<String, Object>> retrieveFinancialAccidentsByIds(String[] ids);
}

2.서비스 
@Service
public class FinancialAccidentService {

    @Autowired
    private FinancialAccidentRepository financialAccidentRepository;

    @Autowired
    private CodeManagementSqlMapper sqlMapper;

    public List<HashMap<String, Object>> getFinancialAccidents(Map<String, String> reqParamMap, String[] checkboxForfinancialAccidentsData) {
        boolean isEditing = isModalOpenedForEditingData(reqParamMap); // isModalOpenedForEditingData 메서드는 필요에 따라 구현
        if (isEditing) {
            String accidentGroupId = StringUtils.trimToEmpty(reqParamMap.get("accidentGroupId"));
            String startDate = StringUtils.trimToEmpty(reqParamMap.get("startDateFormatted"));
            String startTime = StringUtils.trimToEmpty(reqParamMap.get("startTimeFormatted"));
            String endDate = StringUtils.trimToEmpty(reqParamMap.get("endDateFormatted"));
            String endTime = StringUtils.trimToEmpty(reqParamMap.get("endTimeFormatted"));
            int pageSize = Integer.parseInt(reqParamMap.get("sizeOfRows"));
            int currentPage = Integer.parseInt(StringUtils.defaultIfBlank(reqParamMap.get("currentPage"), PagingAction.DEFAULT_OFFSET_SIZE));
            int offset = pageSize * (currentPage - 1);

            return financialAccidentRepository.searchFinancialAccidents(accidentGroupId, startDate, startTime, endDate, endTime, pageSize, offset);
        } else if (checkboxForfinancialAccidentsData != null) {
            return financialAccidentRepository.retrieveFinancialAccidentsByIds(checkboxForfinancialAccidentsData);
        }
        return new ArrayList<>(); // or throw an exception if appropriate
    }

    public List<HashMap<String, Object>> getAccidentTypes() {
        return sqlMapper.getListOfCodeDt("ACCIDENT_TYPE");
    }

    private boolean isModalOpenedForEditingData(Map<String, String> reqParamMap) {
        // Implement your logic to determine if the modal is opened for editing
        return reqParamMap.containsKey("accidentGroupId"); // Example: check if accidentGroupId is present
    }
}


1. 컨트롤러 
@Controller
public class SearchForFinancialAccidentController {

    @Autowired
    private FinancialAccidentService financialAccidentService;

    @RequestMapping("/servlet/nfds/financial_accident/form_of_list_financial_accident")
    public ModelAndView openModalForFormOfFinancialAccidentList(@RequestParam Map<String, String> reqParamMap,
            @RequestParam(value = "checkboxForfinancialAccidentsData", required = false) String[] checkboxForfinancialAccidentsData)
            throws Exception {

        Logger.debug("[SearchForFinancialAccidentController][METHOD : openModalForFormOfFinancialAccidents][EXECUTION]");
        Logger.debug("reqParamMap: " + reqParamMap);

        String startDate = StringUtils.trimToEmpty(reqParamMap.get("startDateFormatted"));
        String startTime = StringUtils.trimToEmpty(reqParamMap.get("startTimeFormatted"));
        String endDate = StringUtils.trimToEmpty(reqParamMap.get("endDateFormatted"));
        String endTime = StringUtils.trimToEmpty(reqParamMap.get("endTimeFormatted"));
        int pageSize = Integer.parseInt(reqParamMap.get("sizeOfRows"));
        int currentPage = Integer.parseInt(StringUtils.defaultIfBlank(reqParamMap.get("currentPage"), PagingAction.DEFAULT_OFFSET_SIZE));

        Logger.debug("startDate: " + startDate);
        Logger.debug("endDate: " + endDate);

        ModelAndView mav = new ModelAndView("search/search_for_state/form_of_list_financial_accident");

        List<HashMap<String, Object>> financialAccidentList = financialAccidentService.getFinancialAccidents(reqParamMap, checkboxForfinancialAccidentsData);

        mav.addObject("financialAccidentList", financialAccidentList);
        mav.addObject("totalSize", financialAccidentList.size()); // 수정 필요: 실제 total size를 서비스에서 받아와야 함
        mav.addObject("pageSize", pageSize);
        mav.addObject("currentPage", currentPage);
        mav.addObject("startDate", startDate);
        mav.addObject("startTime", startTime);
        mav.addObject("endDate", endDate);
        mav.addObject("endTime", endTime);

        mav.addObject("accidentTypes", financialAccidentService.getAccidentTypes());

        return mav;
    }
}