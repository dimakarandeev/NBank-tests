package api.requests.skelethon.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object put(BaseModel model);
    Object get(BaseModel model);
    Object update(long id, BaseModel model);
    Object delete(long id);
}