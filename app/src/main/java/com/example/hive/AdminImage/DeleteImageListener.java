package com.example.hive.AdminImage;

/**
 * Interface to act as a listener in the <code>ConfirmImageDelete</code> fragment, implemented in
 * <code>AdminImageListActivity</code>
 */
public interface DeleteImageListener {
    /**
     * Logic to remove image from firebase, along with any reference to it in event or user
     * documents.
     *
     * @param position
     * int: position of image in the array
     * @param url
     * String: download URL of the image
     * @param id
     * String: id of the document in images collection in firebase that hold info about this image
     * @param relatedDocID
     * String: id of firebase document that references this image
     */
    void onDelete(int position, String url, String id, String relatedDocID);
}
