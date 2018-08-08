pragma solidity ^0.4.24;

import "./Ownable.sol";

contract DistrictTasks is Ownable {
    struct Task {
        uint biddingEndsOn;
        bool isActive;
    }

    Task[] public tasks;


    event LogAddTask (
        uint indexed id,
        string title,
        bool isActive,
        uint biddingEndsOn
    );
    
    event LogUpdateActive(uint indexed id, bool isActive);
    
    event LogUpdateBiddingEndsOn(uint indexed id, uint biddingEndsOn);


    modifier validBiddingEndsOn(uint biddingEndsOn) {
        require(block.timestamp <= biddingEndsOn, "biddingEndsOn has to be in the future");
        _;
    }

    modifier validTitle(string title) {
        require(bytes(title).length > 0, "Title of the task is required");
        _;
    }


    function addTask(string _title, uint _biddingEndsOn, bool _isActive)
    public
    onlyOwner
    validTitle(_title)
    validBiddingEndsOn(_biddingEndsOn)
    {
        uint _id = tasks.push(Task(_biddingEndsOn, _isActive)) - 1;
        emit LogAddTask(
            _id,
            _title,
            _isActive,
            _biddingEndsOn
        );
    }

    function updateActive(uint _id, bool _isActive) public onlyOwner {
        tasks[_id].isActive = _isActive;
        emit LogUpdateActive(_id, _isActive);
    }

    function updateBiddingEndsOn(uint _id, uint _biddingEndsOn)
    public
    onlyOwner
    validBiddingEndsOn(_biddingEndsOn)
    {
        tasks[_id].biddingEndsOn = _biddingEndsOn;
        emit LogUpdateBiddingEndsOn(_id, _biddingEndsOn);
    }

    function countTasks() public view returns (uint){
        return tasks.length;
    }
}
