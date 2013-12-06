
CREATE INDEX ainput_idx ON ainput(id_ajob,id);
CREATE INDEX ajob_idx ON ajob(id_aworkflow,id);
CREATE INDEX aoutput_idx ON aoutput(id_ajob,id);
CREATE INDEX aworkflow_idx ON aworkflow(name);

CREATE INDEX inputprop_idx ON input_prop(id_workflow,id_ainput);
CREATE INDEX jobdesc_idx ON job_desc(id_workflow,id_ajob);
CREATE INDEX jobprop_idx ON job_prop(id_workflow,id_ajob);
CREATE INDEX outputprop_idx ON output_prop(id_workflow,id_aoutput);
CREATE INDEX workflow_idx ON workflow(id_aworkflow,id);

CREATE INDEX history_idx ON history(id_workflow,id_ajob);
CREATE INDEX repository_idx ON repository(type,id);

CREATE INDEX jobstatus_idx USING BTREE ON job_status (id_workflow,id_ajob,wrtid,pid);
