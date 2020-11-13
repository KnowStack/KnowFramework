
drop table if exists `task_info`;
CREATE TABLE `task_info` (
	`id` BIGINT(20) auto_increment,
	`code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'task code',
	`name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '名称',
    `desc` VARCHAR(1000) DEFAULT '' NOT NULL COMMENT '任务描述',
	`cron` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'cron 表达式',
	`class_name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '类的全限定名',
    `params` VARCHAR(1000) DEFAULT '' NOT NULL COMMENT '执行参数 map 形式{key1:value1,key2:value2}',
	`retry` INT(10) DEFAULT 0 NOT NULL COMMENT '允许重试次数',
	`last_fire_time` DATETIME COMMENT '上次执行时间',
	`next_fire_time` DATETIME COMMENT '下次执行时间',
	`timeout` BIGINT(20) DEFAULT 0 NOT NULL COMMENT '超时 毫秒',
	`status` TINYINT(4) DEFAULT 0 NOT NULL COMMENT '1运行中 2暂停',
	`subtasks` VARCHAR(1000) DEFAULT '' NOT NULL COMMENT '子任务code列表,逗号分隔',
	PRIMARY KEY (`id`),
	UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务信息';

drop table if exists `task_lock`;
CREATE TABLE `task_lock` (
    `task_code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'task code',
    `woker_code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'worker code',
    PRIMARY KEY (`task_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务锁';

drop table if exists `job_info`;
CREATE TABLE `job_info` (
	`id` BIGINT(20) auto_increment,
	`code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'task code',
	`task_code` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '任务code',
	`class_name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '类的全限定名',
	`retry_times` INT(10) DEFAULT 0 NOT NULL COMMENT '第几次重试',
	`start_time` DATETIME COMMENT '开始时间',
	PRIMARY KEY (`id`),
	UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='正在执行的job信息';

drop table if exists `job_log`;
CREATE TABLE `job_log` (
	`id` BIGINT(20) auto_increment,
	`code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'job log code',
	`job_code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'job code',
	`task_code` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '任务code',
	`class_name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '类的全限定名',
	`retry_times` INT(10) DEFAULT 0 NOT NULL COMMENT '第几次重试',
	`start_time` DATETIME COMMENT '开始时间',
	`end_time` DATETIME COMMENT '结束时间',
	`status` TINYINT(4) DEFAULT 0 NOT NULL COMMENT '执行结果 1成功 2失败',
	`error` TEXT NOT NULL COMMENT '错误信息',
	PRIMARY KEY (`id`),
	UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job执行历史日志';

drop table if exists `worker_info`;
CREATE TABLE `worker_info` (
	`id` BIGINT(20) auto_increment,
	`code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'job log code',
	`name` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'worker名',
	`cpu` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'cpu',
	`cpu_used` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'cpu使用',
	`memory` VARCHAR(100) DEFAULT '' NOT NULL COMMENT '内存',
	`memory_used` VARCHAR(100) DEFAULT '' NOT NULL COMMENT '内存使用',
	`jvm_heap` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'jvm堆大小',
	`jvm_heap_used` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'jvm堆使用',
	`job_num` INT(10) DEFAULT 0 NOT NULL COMMENT '正在执行job数',
	`heartbeat` DATETIME COMMENT '心跳时间',
	PRIMARY KEY (`id`),
	UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker信息';
