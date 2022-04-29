package com.szhdev.base.mvvm;

/**
 * Created by szhdev on 2020/12/22.
 */
public class BaseViewModelRepo<REP extends BaseRepo> extends BaseViewModel {


    protected REP mRepo;

    public BaseViewModelRepo() {
        Class<REP> rep = ClassUtil.getRepo(this);

        if (rep != null) {

            try {

                mRepo = rep.newInstance();
                mRepo.setIBaseViewAction(this);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

    }

}
